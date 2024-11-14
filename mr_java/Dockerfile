# Usa una imagen base de OpenJDK 8 (requerida por Hadoop)
FROM openjdk:8-jdk

# Variables de entorno para Hadoop y Java
ENV HADOOP_VERSION=3.4.0
ENV HADOOP_HOME=/opt/hadoop
ENV PATH=$PATH:$HADOOP_HOME/bin:$HADOOP_HOME/sbin
ENV JAVA_HOME=/usr/local/openjdk-8

# Instala las dependencias necesarias y descarga Hadoop 3.4.0
RUN apt-get update && apt-get install -y sudo wget python3 openssh-client openssh-server nano && \
    wget -qO- "https://dlcdn.apache.org/hadoop/common/hadoop-$HADOOP_VERSION/hadoop-$HADOOP_VERSION.tar.gz" | tar -xz -C /opt && \
    mv /opt/hadoop-$HADOOP_VERSION /opt/hadoop && \
    apt-get clean

# Establece JAVA_HOME en hadoop-env.sh
RUN echo "export JAVA_HOME=$JAVA_HOME" >> $HADOOP_HOME/etc/hadoop/hadoop-env.sh

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

# Crear un directorio de trabajo para los scripts y datasets
WORKDIR /data

# Sobreescribe el archivo /etc/sudoers con el contenido proporcionado
RUN echo '#\n# Please consider adding local content in /etc/sudoers.d/ instead of\n# directly modifying this file.\n#\n# See the man page for details on how to write a sudoers file.\n#\nDefaults        env_reset\nDefaults        mail_badpass\nDefaults        secure_path="/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"\n\n# Host alias specification\n\n# User alias specification\n\n# Cmnd alias specification\n\n# User privilege specification\nroot    ALL=(ALL:ALL) ALL\n\n# Allow members of group sudo to execute any command\nsudo    ALL=(ALL:ALL) ALL\n\n# See sudoers(5) for more information on "@include" directives:\n\n@includedir /etc/sudoers.d' > /etc/sudoers && chmod 440 /etc/sudoers

# Exponer los puertos necesarios para HDFS y YARN
EXPOSE 8088 9870 22

# Iniciar SSH, HDFS, YARN y preparar el directorio de datos
CMD ["bash", "-c", "service ssh start && \
    $HADOOP_HOME/sbin/start-dfs.sh && \
    $HADOOP_HOME/sbin/start-yarn.sh && \
    hadoop fs -mkdir -p /user/data && \
    hadoop fs -put /data/copenhagen.csv /user/data/ && \
    bash"]
