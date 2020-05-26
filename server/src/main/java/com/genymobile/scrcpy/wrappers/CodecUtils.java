package com.genymobile.scrcpy.wrappers;

import android.media.MediaCodecInfo;
import android.os.Build;

import com.genymobile.scrcpy.Ln;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CodecUtils {

    private static final LinkedHashMap<Integer, String> levelsTable = new LinkedHashMap<Integer, String>() {
        {
            // Adding all possible level and their properties
            // 3rd property, bitrate was added but not sure if needed for now.
            // Source: https://en.wikipedia.org/wiki/Advanced_Video_Coding#Levels
            put(MediaCodecInfo.CodecProfileLevel.AVCLevel1, "485,99,64,1");
            put(MediaCodecInfo.CodecProfileLevel.AVCLevel1b, "485,99,128,1b");
            put(MediaCodecInfo.CodecProfileLevel.AVCLevel11, "3000,396,192,1.1");
            put(MediaCodecInfo.CodecProfileLevel.AVCLevel12, "6000,396,384,1.2");
            put(MediaCodecInfo.CodecProfileLevel.AVCLevel13, "11880,396,768,1.3");
            put(MediaCodecInfo.CodecProfileLevel.AVCLevel2, "11880,396,2000,2");
            put(MediaCodecInfo.CodecProfileLevel.AVCLevel21, "19800,792,4000,2.1");
            put(MediaCodecInfo.CodecProfileLevel.AVCLevel22, "20250,1620,4000,2.2");
            put(MediaCodecInfo.CodecProfileLevel.AVCLevel3, "40500,1620,10000,3");
            put(MediaCodecInfo.CodecProfileLevel.AVCLevel31, "108000,3600,14000,3.1");
            put(MediaCodecInfo.CodecProfileLevel.AVCLevel32, "216000,5120,20000,3.2");
            put(MediaCodecInfo.CodecProfileLevel.AVCLevel4, "245760,8192,20000,4");
            put(MediaCodecInfo.CodecProfileLevel.AVCLevel41, "245760,8192,50000,4.1");
            put(MediaCodecInfo.CodecProfileLevel.AVCLevel42, "522240,8704,50000,4.2");
            put(MediaCodecInfo.CodecProfileLevel.AVCLevel5, "589824,22080,135000,5");
            put(MediaCodecInfo.CodecProfileLevel.AVCLevel51, "983040,36864,240000,5.1");
            put(MediaCodecInfo.CodecProfileLevel.AVCLevel52, "2073600,36864,240000,5.2");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaCodecInfo.CodecProfileLevel.AVCLevel6, "4177920,139264,240000,6");
                put(MediaCodecInfo.CodecProfileLevel.AVCLevel61, "8355840,139264,480000,6.1");
                put(MediaCodecInfo.CodecProfileLevel.AVCLevel62, "16711680,139264,800000,6.2");
            }
        }
    };

    private static final HashMap<Integer, String> profilePrettyNameTable = new HashMap<Integer, String>() {{
        put(0x1, "AVCProfileBaseline");
        put(0x2, "AVCProfileMain");
        put(0x4, "AVCProfileExtended");
        put(0x8, "AVCProfileHigh");
        put(0x10, "AVCProfileHigh10");
        put(0x20, "AVCProfileHigh422");
        put(0x40, "AVCProfileHigh444");
        put(0x10000, "AVCProfileConstrainedBaseline");
        put(0x80000, "AVCProfileConstrainedHigh");
    }};

    /**
     * The purpose of this function is to return the lowest possible codec profile level
     * that supports the given width/height/bitrate of the stream
     * @param width of the device
     * @param height of the device
     * @param bitRate at which we stream
     * @return the lowest possible level that should support the given properties.
     */
    public static int calculateLevel(int width, int height, int bitRate) {
        // Calculations source: https://stackoverflow.com/questions/32100635/vlc-2-2-and-levels
        int macroblocks = (int)( Math.ceil(width/16.0) * Math.ceil(height/16.0) );
        int macroblocks_s = macroblocks * 60;
        for (Map.Entry<Integer, String> entry : levelsTable.entrySet()) {
            String[] levelProperties = entry.getValue().split(",");
            int levelMacroblocks_s = Integer.parseInt(levelProperties[0]);
            int levelMacroblocks = Integer.parseInt(levelProperties[1]);
            if(macroblocks_s > levelMacroblocks_s) continue;
            if(macroblocks > levelMacroblocks) continue;
            Ln.i("Level selected based on screen size calculation: " + levelProperties[3] + " (value: " + entry.getKey() + ")");
            return entry.getKey();
        }
        Ln.i("No calculated level found, returning 0");
        return 0;
    }

    public static String getProfileName(int profile) {
        return profilePrettyNameTable.get(profile);
    }

}
