# harland
API based 

## Usage

Run API (with docker):
```
# Create and run redis
docker run -d -p 6379:6379 -v /tmp/redis-data:/data --name redis dockerfile/redis
 
# Build (dev only)
HARLAND_ID=`docker build .`

# Run with linked redis
docker run -p 8080:8080 --link redis:redis $HARLAND_ID
```
