import subprocess
import time
import json
import threading
import http.client
import statistics
import sys
import os

# Configuración
API_HOST = "localhost"
API_PORT = 8080
ENDPOINT = "/api/recipes"
HEALTH_ENDPOINT = "/health"
TOTAL_REQUESTS = 2000
CONCURRENCY = 20

def run_command(command):
    result = subprocess.run(command, shell=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    return result.stdout.strip()

def get_container_memory(container_name):
    # Obtener uso de memoria en MB
    cmd = f"docker stats --no-stream --format '{{{{.MemUsage}}}}' {container_name}"
    output = run_command(cmd)
    # Output format example: "45.5MiB / 7.6GiB"
    try:
        mem_str = output.split('/')[0].strip()
        if "GiB" in mem_str:
            return float(mem_str.replace("GiB", "")) * 1024
        elif "MiB" in mem_str:
            return float(mem_str.replace("MiB", ""))
        elif "KiB" in mem_str:
            return float(mem_str.replace("KiB", "")) / 1024
        else:
            return float(mem_str.replace("B", "")) / 1024 / 1024
    except:
        return 0.0

def get_startup_time(container_name):
    cmd = f"docker logs {container_name}"
    logs = run_command(cmd)
    for line in logs.split('\n'):
        if "started in" in line:
            # Example: ... started in 2.851s. Listening on: ...
            try:
                parts = line.split("started in")
                time_part = parts[1].split("s.")[0].strip()
                return float(time_part)
            except:
                continue
    return -1.0

def make_request():
    start = time.time()
    try:
        conn = http.client.HTTPConnection(API_HOST, API_PORT, timeout=5)
        conn.request("GET", ENDPOINT)
        resp = conn.getresponse()
        resp.read()
        conn.close()
        duration = (time.time() - start) * 1000 # ms
        return duration, resp.status
    except Exception as e:
        return None, str(e)

def load_test():
    results = []
    errors = 0
    
    def worker():
        nonlocal errors
        while len(results) + errors < TOTAL_REQUESTS:
            duration, status = make_request()
            if duration is not None:
                results.append(duration)
            else:
                errors += 1

    threads = []
    start_time = time.time()
    
    for _ in range(CONCURRENCY):
        t = threading.Thread(target=worker)
        t.start()
        threads.append(t)
        
    for t in threads:
        t.join()
        
    total_time = time.time() - start_time
    
    return {
        "total_requests": len(results),
        "errors": errors,
        "total_time_sec": total_time,
        "throughput_rps": len(results) / total_time,
        "avg_latency_ms": statistics.mean(results) if results else 0,
        "p95_latency_ms": statistics.quantiles(results, n=20)[18] if len(results) > 20 else 0,
        "p99_latency_ms": statistics.quantiles(results, n=100)[98] if len(results) > 100 else 0,
        "min_latency_ms": min(results) if results else 0,
        "max_latency_ms": max(results) if results else 0
    }

def main():
    if len(sys.argv) < 3:
        print("Usage: python3 run_benchmark.py <image_name> <container_name>")
        sys.exit(1)

    image_name = sys.argv[1]
    container_name = sys.argv[2]
    
    print(f"=== Benchmarking {image_name} ===")
    
    # 1. Start Container
    print("Starting container...")
    # Usamos las credenciales de application-devlocal.properties
    env_vars = (
        "-e QUARKUS_DATASOURCE_DB_KIND=postgresql "
        "-e QUARKUS_DATASOURCE_USERNAME=neondb_owner "
        "-e QUARKUS_DATASOURCE_PASSWORD=npg_wHpyzmq02ZDW "
        "-e QUARKUS_DATASOURCE_JDBC_URL='jdbc:postgresql://ep-black-wind-a94um1q6-pooler.gwc.azure.neon.tech/neondb?sslmode=require&channel_binding=require' "
        "-e QUARKUS_LOG_LEVEL=INFO "
        "-e QUARKUS_HIBERNATE_ORM_DATABASE_GENERATION=none "
    )
    
    run_command(f"docker stop {container_name} || true")
    run_command(f"docker rm {container_name} || true")
    
    run_command(f"docker run -d --name {container_name} -p {API_PORT}:{API_PORT} {env_vars} {image_name}")
    
    # 2. Wait for Health
    print("Waiting for health check...")
    attempts = 0
    started = False
    while attempts < 30:
        try:
            conn = http.client.HTTPConnection(API_HOST, API_PORT)
            conn.request("GET", HEALTH_ENDPOINT)
            resp = conn.getresponse()
            if resp.status == 200:
                started = True
                break
        except:
            pass
        time.sleep(1)
        attempts += 1
        
    if not started:
        print("Container failed to start or pass health check")
        print(run_command(f"docker logs {container_name}"))
        run_command(f"docker stop {container_name}")
        sys.exit(1)

    # 3. Metrics - Startup & Idle Memory
    startup_time = get_startup_time(container_name)
    time.sleep(2) # Stabilize
    idle_memory = get_container_memory(container_name)
    
    print(f"Startup Time: {startup_time}s")
    print(f"Idle Memory: {idle_memory:.2f} MB")
    
    # 4. Load Test
    print(f"Running Load Test ({TOTAL_REQUESTS} requests, {CONCURRENCY} concurrency)...")
    load_metrics = load_test()
    
    # 5. Metrics - Loaded Memory
    loaded_memory = get_container_memory(container_name)
    print(f"Loaded Memory: {loaded_memory:.2f} MB")
    
    # 6. Cleanup
    run_command(f"docker stop {container_name}")
    run_command(f"docker rm {container_name}")
    
    # 7. Report
    report = {
        "image": image_name,
        "startup_time_s": startup_time,
        "memory_idle_mb": idle_memory,
        "memory_loaded_mb": loaded_memory,
        "load_test": load_metrics
    }
    
    print(json.dumps(report, indent=2))
    
    # Save to file
    with open(f"benchmarks/{container_name}_report.json", "w") as f:
        json.dump(report, f, indent=2)

if __name__ == "__main__":
    main()
