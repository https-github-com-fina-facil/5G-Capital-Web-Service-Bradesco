mvn deploy:deploy-file -DgroupId=br.com.bradesco.webta.clientepj.webservices.WSWEBTAProxy \
  -DartifactId=br.com.bradesco.webta.clientepj.webservices.WSWEBTAProxy \
  -Dpackaging=<type-of-packaging> \
  -Dfile=${basedir}/src/main/resources/lib/wswebta-client-v.1.1.jar \
  