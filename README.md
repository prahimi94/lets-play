command for creating Mongo db container:
  docker run -d --name letsplay-mongo -p 27017:27017 -e MONGO_INITDB_DATABASE=letsplay mongo:6
