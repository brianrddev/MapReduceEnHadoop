import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class TemperatureClassification {

    // Mapper Class
    public static class TemperatureMapper extends Mapper<Object, Text, Text, IntWritable> {
        // Constante para representar un conteo de "1"
        private static final IntWritable one = new IntWritable(1);
        private Text category = new Text(); // Llave para la categoría ("hot", "cold", "neutral")

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            // Convertir la línea de entrada a un String
            String line = value.toString();

            // Dividir la línea en campos separados por comas
            String[] fields = line.split(",");

            // Verificar que la línea tenga al menos 3 campos
            if (fields.length >= 3) {
                try {
                    // Convertir el tercer campo (temperatura) a un valor numérico
                    double temperature = Double.parseDouble(fields[2]);

                    // Clasificar la temperatura en una categoría
                    if (temperature > 20.0) {
                        category.set("hot"); // Temperatura mayor a 20 -> "hot"
                    } else if (temperature < 5.0) {
                        category.set("cold"); // Temperatura menor a 5 -> "cold"
                    } else {
                        category.set("neutral"); // Rango intermedio -> "neutral"
                    }

                    // Emitir la categoría junto con un conteo de "1"
                    context.write(category, one);
                } catch (NumberFormatException e) {
                    // Ignorar líneas con valores no numéricos en la temperatura
                }
            }
        }
    }

    // Reducer Class
    public static class TemperatureReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable(); // Resultado final para cada categoría

        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;

            // Sumar todos los valores asociados con una clave (categoría)
            for (IntWritable val : values) {
                sum += val.get();
            }

            // Establecer el resultado y emitir la clave junto con el conteo
            result.set(sum);
            context.write(key, result);
        }
    }

    // Driver Code
    public static void main(String[] args) throws Exception {
        // Crear una configuración para el trabajo MapReduce
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "temperature classification"); // Asignar un nombre al trabajo

        // Configurar la clase principal del trabajo
        job.setJarByClass(TemperatureClassification.class);

        // Configurar las clases Mapper, Combiner (opcional) y Reducer
        job.setMapperClass(TemperatureMapper.class);
        job.setCombinerClass(TemperatureReducer.class); // Combiner optimiza el procesamiento
        job.setReducerClass(TemperatureReducer.class);

        // Configurar el tipo de salida del Mapper y Reducer
        job.setOutputKeyClass(Text.class); // Llave de salida: categoría
        job.setOutputValueClass(IntWritable.class); // Valor de salida: conteo

        // Configurar las rutas de entrada y salida desde los argumentos del programa
        FileInputFormat.addInputPath(job, new Path(args[0])); // Ruta de entrada
        FileOutputFormat.setOutputPath(job, new Path(args[1])); // Ruta de salida

        // Ejecutar el trabajo y finalizar con éxito o error
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
