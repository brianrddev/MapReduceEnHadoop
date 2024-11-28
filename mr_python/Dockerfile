# Usa una imagen base de OpenJDK 8 (requerida por Hadoop)
FROM openjdk:8-jdk

# Variables de entorno para Hadoop y Java
ENV HADOOP_VERSION=3.4.0
ENV HADOOP_HOME=/opt/hadoop
ENV PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin
ENV JAVA_HOME=/usr/local/openjdk-8

# Instala las dependencias necesarias y descarga Hadoop 3.4.0
RUN apt-get update && \
    apt-get install -y wget python3 openssh-client openssh-server nano && \
    wget -qO- "https://dlcdn.apache.org/hadoop/common/hadoop-$HADOOP_VERSION/hadoop-$HADOOP_VERSION.tar.gz" | \
    tar -xz -C /opt && \
    mv /opt/hadoop-$HADOOP_VERSION /opt/hadoop && \
    apt-get clean

# Configuración para evitar el uso de sudo en Hadoop
RUN echo 'export HDFS_NAMENODE_USER=root' >> $HADOOP_HOME/etc/hadoop/hadoop-env.sh && \
    echo 'export HDFS_DATANODE_USER=root' >> $HADOOP_HOME/etc/hadoop/hadoop-env.sh && \
    echo 'export HDFS_SECONDARYNAMENODE_USER=root' >> $HADOOP_HOME/etc/hadoop/hadoop-env.sh && \
    echo 'export YARN_RESOURCEMANAGER_USER=root' >> $HADOOP_HOME/etc/hadoop/yarn-env.sh && \
    echo 'export YARN_NODEMANAGER_USER=root' >> $HADOOP_HOME/etc/hadoop/yarn-env.sh

# Configuración de SSH para Hadoop
RUN ssh-keygen -q -N "" -f /root/.ssh/id_rsa && \
    cat /root/.ssh/id_rsa.pub >> /root/.ssh/authorized_keys && \
    chmod 600 /root/.ssh/authorized_keys && \
    service ssh start

# Crear directorios necesarios
RUN mkdir -p /data /scripts /outputs && \
    mkdir -p /tmp/hadoop-root/dfs/name && \
    mkdir -p /tmp/hadoop-root/dfs/data && \
    chmod -R 777 /data /scripts /outputs /tmp/hadoop-root

# Exponer los puertos necesarios para HDFS y YARN
EXPOSE 8088 9870 22

# Iniciar SSH y abrir una sesión interactiva
CMD ["bash", "-c", "service ssh start && exec bash"]
