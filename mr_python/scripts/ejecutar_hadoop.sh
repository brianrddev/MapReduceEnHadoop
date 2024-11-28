#!/bin/bash

# Obtener la fecha y hora actuales en el formato YYYYMMDD_HHMMSS
timestamp=$(date +"%Y%m%d_%H%M%S")

# Definir el directorio de salida en el sistema de archivos local
local_output_dir="/outputs/output_$timestamp"

# Crear el directorio de salida en el sistema de archivos local
mkdir -p $local_output_dir

# Definir el directorio de salida en HDFS
hdfs_output_dir="/user/output_$timestamp"

# Ejecutar el trabajo de Hadoop Streaming
hadoop jar $HADOOP_HOME/share/hadoop/tools/lib/hadoop-streaming-*.jar \
  -input /user/data/copenhagen.csv \
  -output $hdfs_output_dir \
  -mapper "python3 /scripts/clima_mapper.py" \
  -reducer "python3 /scripts/clima_reducer.py"

# Verificar si el trabajo de Hadoop se ejecutó correctamente
if [ $? -eq 0 ]; then
  echo "El trabajo de Hadoop se completó con éxito. Copiando resultados al sistema de archivos local..."

  # Copiar el archivo part-00000 desde HDFS al sistema de archivos local
  hadoop fs -copyToLocal $hdfs_output_dir/part-00000 $local_output_dir/

  mv /outputs/output_$timestamp/part-00000 /outputs/output_$timestamp.txt
  rm -r /outputs/output_$timestamp


  echo "Los resultados se han guardado en: $local_output_dir"
else
  echo "El trabajo de Hadoop falló. Por favor, revisa los logs para más detalles."
fi
