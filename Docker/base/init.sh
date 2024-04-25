# Update config file
sed -i -e "s#__replace.database__#$DATABASE#" ${PROPERTIES_PATH}
sed -i -e "s#__replace.luxand.key__#$LUXAND_KEY#" ${PROPERTIES_PATH}
/opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0 --debug