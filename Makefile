.PHONY: test
test:
	docker pull neo4j:3.3.3
	docker run --detach --publish=7474:7474 --publish=7687:7687 --volume=/tmp/neo4j/data:/data --env=NEO4J_AUTH=none --name neo4jprofilescontainer neo4j:3.3.3
	sleep 15 #Give the container time to start
	sbt test
	docker stop neo4jprofilescontainer
	docker rm neo4jprofilescontainer
	rm -rf /tmp/neo4j

.PHONY: run-local
run-local:
	#docker ps -aq --no-trunc | xargs docker rm #Remove all stopped containers
	docker pull neo4j:3.3.3
	docker run --detach --publish=7474:7474 --publish=7687:7687 --volume=/tmp/neo4j/data:/data --env=NEO4J_AUTH=none neo4j:3.3.3
	sleep 15 #Give the container time to start
	sbt run
