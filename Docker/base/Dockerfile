FROM jboss/wildfly:10.1.0.Final

USER root

# Install OpenCV
RUN set -x \
    # Install dependencies
    && yum update -y && yum install -y \
    git build-essential make cmake git libgtk2.0-dev pkg-config libavcodec-dev libavformat-dev libswscale-dev gcc gcc-c++ \
    python-dev python-numpy libtbb2 libtbb-dev libjpeg-dev libpng-dev libtiff-dev libjasper-dev libdc1394-22-dev ant \
    # Download sources and compile OpenCV libraries
    && mkdir -p /home/opencv \
    && git clone https://github.com/opencv/opencv.git /home/opencv \
    && cd /home/opencv  && mkdir -p /home/opencv/release && cd /home/opencv/release \
    && cmake -D CMAKE_BUILD_TYPE=RELEASE -D CMAKE_INSTALL_PREFIX=/usr/local .. \
    && make && make install \
    # Generate library OpenCV for Java
    # && mkdir -p /home/opencv/release-java && cd /home/opencv/release-java \
    # && cmake -DBUILD_SHARED_LIBS=OFF .. \
    # && make -j8 \
    # Clean image
    && rm -rf /home/opencv

# Configure WildFly
RUN /opt/jboss/wildfly/bin/add-user.sh admin admin123# --silent

EXPOSE 8443

ENV SSL_KEY_PASSWORD=123456 \
    SSL_KEYSTORE_PASSWORD=123456 \
    LD_LIBRARY_PATH="/home/wildfly/config/libs:/usr/local/libs:${LD_LIBRARY_PATH}" \
    PROPERTIES_PATH=/home/wildfly/config/smilecounter.config \
    DATABASE=mongodb://smilecounter-db:27017/smilecounter

RUN cd /opt/jboss/wildfly/standalone/configuration/ \
    && keytool -genkey -alias server -noprompt -dname "CN=smilecounter.org, OU=ID, O=Smilecounter, L=Gdansk, S=Pomorze, C=PL" \
       -keyalg RSA -keystore server.keystore -validity 365 -keypass ${SSL_KEY_PASSWORD} -storepass ${SSL_KEYSTORE_PASSWORD}

ADD smilecounter /home/wildfly/config/
COPY standalone.xml /opt/jboss/wildfly/standalone/configuration/

COPY init.sh /
CMD ["/bin/bash", "-c", "/init.sh"]