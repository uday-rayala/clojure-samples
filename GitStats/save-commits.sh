pushd .
cd ~/Projects/core
git log --date=short --pretty="format:%cd %s" > /Users/rudayaku/Projects/clojure-samples/GitStats/core-commits.txt
cd ~/Projects/casper-aim/
git log --date=short --pretty="format:%cd %s" > /Users/rudayaku/Projects/clojure-samples/GitStats/aim-commits.txt
popd