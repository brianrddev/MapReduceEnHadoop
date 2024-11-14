import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class TemperatureClassification {

    public static class TemperatureMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        private static final int HOT_THRESHOLD = 20;
        private static final int COLD_THRESHOLD = 5;

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            // Log input value for debugging
            System.out.println("Processing line: " + value.toString());

            String[] fields = value.toString().split(",");

            // Log field count
            System.out.println("Number of fields: " + fields.length);

            // Mejorado el manejo de datos y validación
            if (fields.length < 2) {
                System.out.println("Skipping line: insufficient fields");
                return;
            }

            if (fields[1].equals("temp") || fields[1].trim().isEmpty()) {
                System.out.println("Skipping header or empty temperature field");
                return;
            }

            try {
                // Limpieza de datos mejorada
                String tempStr = fields[1].trim().replace("\"", "");
                float temperatureFloat = Float.parseFloat(tempStr);
                int temperature = Math.round(temperatureFloat);

                System.out.println("Parsed temperature: " + temperature);

                String classification;
                if (temperature > HOT_THRESHOLD) {
                    classification = "Caluroso";
                } else if (temperature < COLD_THRESHOLD) {
                    classification = "Frío";
                } else {
                    System.out.println("Temperature " + temperature + " is in moderate range, skipping");
                    return;
                }

                System.out.println("Emitting: " + classification + " -> 1");
                context.write(new Text(classification), new IntWritable(1));

            } catch (NumberFormatException e) {
                System.err.println("Error parsing temperature from value: " + fields[1]);
                System.err.println("Error details: " + e.getMessage());
            }
        }
    }

    public static class TemperatureReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int sum = 0;
            int count = 0;

            for (IntWritable value : values) {
                sum += value.get();
                count++;
            }

            System.out.println("Reducing for key: " + key.toString() +
                             ", processed " + count + " values, sum = " + sum);

            context.write(key, new IntWritable(sum));
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: TemperatureClassification <input path> <output path>");
            System.exit(-1);
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Temperature Classification");

        job.setJarByClass(TemperatureClassification.class);
        job.setMapperClass(TemperatureMapper.class);
        job.setReducerClass(TemperatureReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.out.println("Input path: " + args[0]);
        System.out.println("Output path: " + args[1]);

        boolean success = job.waitForCompletion(true);
        System.exit(success ? 0 : 1);
    }
}