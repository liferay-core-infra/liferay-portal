CATALINA_OPTS="$CATALINA_OPTS -Dfile.encoding=UTF-8 -Djava.net.preferIPv4Stack=true -Duser.timezone=GMT -Xms2560m -Xmx2560m -XX:MaxNewSize=1536m -XX:MaxMetaspaceSize=768m -XX:MetaspaceSize=768m -XX:NewSize=1536m -XX:SurvivorRatio=7"

export JDK_JAVA_OPTIONS="${JDK_JAVA_OPTIONS} --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.invoke=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/java.net=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/sun.net.www.protocol.http=ALL-UNNAMED --add-opens=java.base/sun.net.www.protocol.https=ALL-UNNAMED --add-opens=java.base/sun.util.calendar=ALL-UNNAMED --add-opens=java.rmi/sun.rmi.transport=ALL-UNNAMED --add-opens=jdk.naming.dns/com.sun.jndi.dns=java.naming --add-opens=jdk.zipfs/jdk.nio.zipfs=ALL-UNNAMED"

#
# Application Class Data Sharing preparses the classes loaded during startup and
# memory maps them on the next boot. Java writes the archive on the first clean
# shutdown and rewrites it whenever the deployed code no longer matches it, so
# it needs no build step. Java 19 introduced "AutoCreateSharedArchive", and an
# older Java refuses to start when it is passed the flag.
#

JAVA_SPECIFICATION_VERSION=$( \
	"${JAVA_HOME:+${JAVA_HOME}/bin/}java" \
		-XshowSettings:properties \
		-version 2>&1 | \
	sed --expression "s/^ *java.specification.version = //p" --quiet)

if [[ "${JAVA_SPECIFICATION_VERSION}" -ge 19 ]]
then
	CATALINA_OPTS="${CATALINA_OPTS} -XX:+AutoCreateSharedArchive -XX:SharedArchiveFile=${CATALINA_BASE}/work/liferay.jsa"
fi
