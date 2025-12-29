#! /bin/sh

# Build the Docker image
echo "Building Docker image..."
docker build -t backenddevtest:latest .

# Exit if build failed
if [ $? -ne 0 ]; then
    echo "Docker build failed. Exiting."
    exit 1
fi

# Docker Compose creates a network named <directory>_default
# Connect to it so the app can reach the simulado service
NETWORK_NAME="backenddevtest-main_default"

# Check if the network exists
if ! docker network inspect $NETWORK_NAME >/dev/null 2>&1; then
    echo "Error: Network '$NETWORK_NAME' not found."
    echo "Please start the docker-compose services first:"
    echo "  docker-compose up -d simulado influxdb grafana"
    exit 1
fi

# Run the container:
# - Connect to docker-compose network to reach simulado service
# - Override API URL to use service name 'simulado' and internal port 80
# - Expose port 5000 to host
echo "Starting container on port 5000..."
docker run -p 5000:5000 \
    --network $NETWORK_NAME \
    -e PRODUCT_API_URL=http://simulado:80 \
    backenddevtest:latest
