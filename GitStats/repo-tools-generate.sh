export GIT_LOGS_DIR=/home/casper/graphs/clojure-samples/GitStats/logs
export GIT_REPOS_DIR=/home/casper/graphs/repos

while [ true ]
do
  ./generate.sh
  sleep 120
done