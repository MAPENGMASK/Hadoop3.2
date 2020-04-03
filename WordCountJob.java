import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


import java.io.IOException;

/**
 * 单词计数
 * 读取test.txt文件，计算每个单词出现的次数
 * 结果：
 * xxx 3
 * jjj  4
 */
public class WordCountJob {
    /**
     * 定义mapper类
     */
    public static class myMapper extends Mapper<LongWritable, Text, Text, LongWritable>{
        /**
         * 接收k1,v1，产生k2,v2
         * @param k1 行首偏移量
         * @param v1 每一行内容
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        protected void map(LongWritable k1, Text v1, Context context) throws IOException, InterruptedException {
            //分割单词
            String[] words = v1.toString().split(" ");
            //封装成<k2,v2>
            for (String word:words){
                Text k2 = new Text(word);
                LongWritable v2 = new LongWritable(1);
                System.out.println("k2:" + word + "     v2:1");
                //写出<k2,v2>
                context.write(k2,v2);
            }
        }
    }

    /**
     * 定义reduce类
     */
    public static class myReduce extends Reducer<Text,LongWritable,Text,LongWritable>{
        /**
         * 对v2s累加求和，输出 k3,v3
         * @param k2 单词键
         * @param v2s 记录列表
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        protected void reduce(Text k2, Iterable<LongWritable> v2s, Context context) throws IOException, InterruptedException {
            long sum = 0;
            for (LongWritable v2:v2s){
                sum += v2.get();
            }
            //组装k3，v3
            Text k3 = k2;
            LongWritable v3 = new LongWritable(sum);
            System.out.println("k3:" + k3.toString() + "    v3:" + sum);
            //输出 k3， v3
            context.write(k3, v3);
        }
    }

    /**
     * 组装job = map + reduce
     * @param args
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        //判断路径参数
        if (args.length != 2)
            return;
        // job配置参数设定
        Configuration conf = new Configuration();
        // 创建job
        Job job =Job.getInstance(conf);

        //注意
        job.setJarByClass(WordCountJob.class);

        //输入路径
        FileInputFormat.setInputPaths(job, new Path(args[0]));
        //输出路径（不存在目录）
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        //指定map
        job.setMapperClass(myMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);
        //指定reduce
        job.setReducerClass(myReduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        //提交job
        job.waitForCompletion(true);
    }
}
