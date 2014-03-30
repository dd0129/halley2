package com.dianping.data.warehouse.utils;

import com.dianping.data.warehouse.common.GlobalResource;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;

/**
 * Created by adima on 14-3-30.
 */
public class Utilities {
    private static Logger logger = LoggerFactory.getLogger(Utilities.class);

    public static class ParameterUtils {
        public static String resourceParamHandle(String para){
            for(Map.Entry<String,String> entry : GlobalResource.ENV_PROPS.entrySet()){
                para = StringUtils.replace(para, entry.getKey(), entry.getValue());
            }
            return para;
        }
    }
    /**
     * StreamWriter.
     *
     */
    public static class StreamWriter extends Thread {
        InputStream is;
        FileOutputStream output;
        FileOutputStream snapOutput;

        public StreamWriter(InputStream is, File file, File snapshotFile) throws IOException{
            this.is = is;
            output = new FileOutputStream(file, true);
            snapOutput = new FileOutputStream(snapshotFile, true);
        }

        @Override
        public void run() {
            BufferedReader br = null;
            try {
                InputStreamReader isr = new InputStreamReader(is,"UTF-8");
                br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null) {
                    output.write((line + "\r\n").getBytes());
                    snapOutput.write((line + "\r\n").getBytes());
                }
                output.close();
                output = null;
                snapOutput.close();
                snapOutput = null;
                br.close();
                br=null;
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }finally{
                //IOUtils.closeStream(br);
                cleanup(output,snapOutput,br);
            }
        }

        public static void cleanup(java.io.Closeable... closeables) {
            for (java.io.Closeable c : closeables) {
                if (c != null) {
                    try {
                        c.close();
                    } catch(Throwable e) {
                        logger.error("Exception in closing " + c, e);
                    }
                }
            }
        }
    }
}
