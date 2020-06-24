package com.kuyou.rtcm.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.util.DisplayMetrics;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

@SuppressLint({"DefaultLocale"})
public class Utils
{
  public static int BD_MESSAGE_FREQUNENCY = 0;
  public static int COUNT_DOWN_TIME = 0;
  public static final int HANDLER_LOCATION_STATUS = 10000;
  public static String INIT_LOCATION_STATUS;
  public static int LOCATION_FRENQUENCY = 0;
  private static int MESSAGE_MAX_LENGHTH = 0;
  private static int NOTIFICATION_ID = 0;
  private static double PI = 0.0D;
  public static int RNSS_CURRENT_LOCATION_MODEL = 0;
  private static final String TAG = "Utils";
  private static char[] arrayOfChar;
  private static final double bj54a = 6378245.0D;
  private static final double bj54f = 298.30000000000001D;
  private static AlertDialog checkBDLocationPortDialog;
  private static AlertDialog checkBDMessagePortDialog;
  public static boolean checkWakeLock = false;
  public static boolean isCycleLocation;
  public static boolean isImmediateLocation;
  public static boolean isProgressDialogShowing;
  public static boolean isStopCycleMessage;
  private static DisplayMetrics metriscs;
  private static SimpleDateFormat sdf;
  public static boolean smsNotificationShow;
  public static boolean phoneContactShow;
  public static int MessageNotificationID = 1001;
  public static int ReportNotificationID = 1002;
  public static int SosNotificationID = 1003;

  static{
    MESSAGE_MAX_LENGHTH = 0;
    NOTIFICATION_ID = 0;
    COUNT_DOWN_TIME = 0;
    BD_MESSAGE_FREQUNENCY = 0;
    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    isImmediateLocation = false;
    isProgressDialogShowing = false;
    isCycleLocation = false;
    LOCATION_FRENQUENCY = 0;
    INIT_LOCATION_STATUS = "";
    RNSS_CURRENT_LOCATION_MODEL = 0;
    isStopCycleMessage = false;
    checkBDLocationPortDialog = null;
    checkBDMessagePortDialog = null;
    smsNotificationShow = false;
  }

  public static boolean CheckGatheredMsg(String paramString){
    return Pattern.compile("集合点坐标\\(([0-9]*.{1}[0-9]*,{1}[0-9]*.{1}[0-9]*)\\)").matcher(paramString).find();
  }

  public static BDLocation DD_GOSS(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4){
    double d1 = paramDouble2 * PI / 180.0D;
    double d2 = Math.sin(d1);
    double d3 = Math.cos(d1);
    double d4 = d2 / d3;
    double d5 = d4 * d4;
    double d6 = (40682009280025.0D - 6356863.2092864998D * 6356863.2092864998D) / 40682009280025.0D;
    double d7 = d3 * Math.sqrt((40682009280025.0D - 6356863.2092864998D * 6356863.2092864998D) / (6356863.2092864998D * 6356863.2092864998D));
    double d8 = d7 * d7;
    double d9 = (paramDouble1 - (3.0D + 6.0D * Math.floor(paramDouble1 / 6.0D))) * PI / 180.0D;
    double d10 = 6378245.0D / Math.sqrt(1.0D - d2 * (d6 * d2));
    double d11 = 1.0D + 3.0D * d6 / 4.0D + 45.0D * (d6 * d6) / 64.0D + 175.0D * (d6 * (d6 * d6)) / 256.0D + 11025.0D * (d6 * (d6 * (d6 * d6))) / 16384.0D;
    double d12 = 3.0D * d6 / 4.0D + 15.0D * (d6 * d6) / 16.0D + 525.0D * (d6 * (d6 * d6)) / 512.0D + 2205.0D * (d6 * (d6 * (d6 * d6))) / 2048.0D;
    double d13 = 15.0D * (d6 * d6) / 64.0D + 105.0D * (d6 * (d6 * d6)) / 256.0D + 2205.0D * (d6 * (d6 * (d6 * d6))) / 4096.0D;
    double d14 = 35.0D * (d6 * (d6 * d6)) / 512.0D + 315.0D * (d6 * (d6 * (d6 * d6))) / 2048.0D;
    double d15 = 6378245.0D * (d11 * paramDouble2 * PI / 180.0D - d3 * (d12 * d2) + d3 * (d13 * d2) * (d3 * (2.0D * d3) - 1.0D) - d14 / 3.0D * (3.0D * d2 - d2 * (d2 * (4.0D * d2))) * (d3 * (d3 * (4.0D * d3)) - 3.0D * d3)) * (1.0D - d6) + d3 * (d2 * (d9 * (d10 * d9))) / 2.0D + (5.0D - d5 + 9.0D * d8 + d8 * (4.0D * d8)) * (d3 * (d3 * (d3 * (d2 * (d9 * (d9 * (d9 * (d10 * d9))))))) / 24.0D) + (61.0D - 58.0D * d5 + d5 * d5) * (d2 * (d10 * Math.pow(d9, 6.0D)) * Math.pow(d3, 5.0D) / 720.0D);
    double d16 = d3 * (d10 * d9) + d3 * (d3 * (d3 * (d9 * (d9 * (d10 * d9))))) * (d8 + (1.0D - d5)) / 6.0D;
    double d17 = d9 * d3;
    double d18 = 500000.0D + (d16 + d17 * (d17 * (d17 * (d17 * d17))) * (d10 * d9) / 120.0D * (5.0D - 18.0D * d5 + d5 * d5 + 14.0D * d8 - d5 * (58.0D * d8))) + 1000000.0D * (1.0D + Math.floor(paramDouble1 / 6.0D));
    double d19 = paramDouble3 - paramDouble4;
    BDLocation localBDLocation = new BDLocation();
    localBDLocation.setLatitude(d18);
    localBDLocation.setLongitude(d15);
    localBDLocation.setEarthHeight(d19);
    return localBDLocation;
  }

  public static BDLocation DD_GOSS2(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, double paramDouble9, double paramDouble10){
    BDLocation localBDLocation = new BDLocation();
    double[] arrayOfDouble = new double[6];
    double d1 = 0.0D;
    double d2 = 0.0D;
    double d3 = 0.0D;
    double d4 = 0.0D;
    double d5 = 0.0D;
    double d6 = 0.0D;
    for (int i = 1; i < 6; i++){
        double d8 = d2 * arrayOfDouble[0] + arrayOfDouble[1] * Math.sin(2.0D * d2) + arrayOfDouble[2] * Math.sin(4.0D * d2) + (arrayOfDouble[3] * Math.sin(6.0D * d2) + arrayOfDouble[4] * Math.sin(8.0D * d2) + arrayOfDouble[5] * Math.sin(10.0D * d2));
        double d9 = Math.tan(d2);
        double d10 = d5 / Math.sqrt(1.0D - d3 * Math.sin(d2) * Math.sin(d2));
        double d11 = d4 * Math.cos(d2) * Math.cos(d2);
        double d12 = d1 * Math.cos(d2);
        double d13 = d8 + d12 * (d12 * (d10 * d9)) / 2.0D + d9 * (d10 * (5.0D - d9 * d9 + 9.0D * d11 + d11 * (4.0D * d11))) * Math.pow(d12, 4.0D) / 24.0D + d9 * (d10 * Math.pow(d12, 6.0D)) * (61.0D - d9 * (58.0D * d9) + d9 * (d9 * (d9 * d9))) / 720.0D;
        double d14 = d10 * d12 + d10 * Math.pow(d12, 3.0D) * (d11 + (1.0D - d9 * d9)) / 6.0D + d10 * (5.0D - d9 * (18.0D * d9) + d9 * (d9 * (d9 * d9)) + 14.0D * d11 - d9 * (d9 * (58.0D * d11))) * Math.pow(d12, 5.0D) / 120.0D;
        double d15 = d13 * paramDouble6;
        double d16 = d14 * paramDouble6;
        double d17 = d15 + paramDouble5;
        localBDLocation.setLongitude(d16 + paramDouble4);
        localBDLocation.setLatitude(d17);
        localBDLocation.setEarthHeight(paramDouble3);
        double d7 = paramDouble1 - paramDouble7;
        if (d7 < -350.0D)
          d7 += 360.0D;
        d1 = d7 * (PI / 180.0D);
        d2 = paramDouble2 * (PI / 180.0D);
        d3 = 2.0D / paramDouble9 - 1.0D / paramDouble9 / paramDouble9;
        d4 = d3 / (1.0D + -d3);
        d5 = paramDouble8 + paramDouble10 * (1.0D - d3 * Math.sin(d2) * Math.sin(d2)) / Math.sqrt(1.0D - d3);
        arrayOfDouble[0] = (1.0D + (43659.0D * Math.pow(d3, 5.0D) / 65536.0D + 11025.0D * Math.pow(d3, 4.0D) / 16384.0D));
        arrayOfDouble[0] += 45.0D * (d3 * d3) / 64.0D + 175.0D * Math.pow(d3, 3.0D) / 256.0D + 0.75D * d3;
        arrayOfDouble[1] = (0.75D * d3 + 15.0D * (d3 * d3) / 16.0D + 525.0D * Math.pow(d3, 3.0D) / 512.0D);
        arrayOfDouble[1] += 2205.0D * Math.pow(d3, 4.0D) / 2048.0D + 72765.0D * Math.pow(d3, 5.0D) / 65536.0D;
        arrayOfDouble[2] = (15.0D * (d3 * d3) / 64.0D + 105.0D * Math.pow(d3, 3.0D) / 256.0D);
        arrayOfDouble[2] += 2205.0D * Math.pow(d3, 4.0D) / 4096.0D + 10395.0D * Math.pow(d3, 5.0D) / 16384.0D;
        arrayOfDouble[3] = (35.0D * Math.pow(d3, 3.0D) / 512.0D + 315.0D * Math.pow(d3, 4.0D) / 2048.0D);
        arrayOfDouble[3] += 31185.0D * Math.pow(d3, 5.0D) / 131072.0D;
        arrayOfDouble[4] = (315.0D * Math.pow(d3, 4.0D) / 16384.0D + 3465.0D * Math.pow(d3, 5.0D) / 65536.0D);
        arrayOfDouble[5] = (693.0D * Math.pow(d3, 5.0D) / 131072.0D);
        d6 = d5 * (1.0D + -d3);
        arrayOfDouble[0] = (d6 * arrayOfDouble[0]);
        arrayOfDouble[i] *= d6 * Math.pow(-1.0D, 1.0D * i) / (2.0D * i);
    }
    return localBDLocation;
  }

  public static double Distance(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4){
    BDLocation localBDLocation1 = LBToGOSS(paramDouble1, paramDouble2, 0.0D);
    BDLocation localBDLocation2 = LBToGOSS(paramDouble3, paramDouble4, 0.0D);
    double d1 = localBDLocation1.getLongitude();
    double d2 = localBDLocation1.getLatitude();
    double d3 = localBDLocation2.getLongitude();
    double d4 = localBDLocation2.getLatitude();
    return Math.sqrt((d1 - d3) * (d1 - d3) + (d2 - d4) * (d2 - d4));
  }

  public static double Distance(double paramDouble1, double paramDouble2, float paramFloat1, double paramDouble3, double paramDouble4, float paramFloat2){
    double d = Distance(paramDouble1, paramDouble2, paramDouble3, paramDouble4);
    return Math.sqrt(d * d + (paramFloat2 - paramFloat1) * (paramFloat2 - paramFloat1));
  }

  public static double Distance(double paramDouble1, double paramDouble2, short paramShort1, double paramDouble3, double paramDouble4, short paramShort2){
    double d = Distance(paramDouble1, paramDouble2, paramDouble3, paramDouble4);
    return Math.sqrt(d * d + (paramShort2 - paramShort1) * (paramShort2 - paramShort1));
  }

  public static String Double2Str(double paramDouble){
    double d1 = 60.0D * (60.0D * paramDouble);
    int i = (int)d1 / 60 / 60;
    int j = (int)(d1 / 60.0D - 60 * (int)(d1 / 60.0D / 60.0D));
    double d2 = d1 - 60 * (60 * (int)(d1 / 60.0D / 60.0D)) - 60 * (int)(d1 / 60.0D - 60 * (int)(d1 / 60.0D / 60.0D));
    Object[] arrayOfObject = new Object[3];
    arrayOfObject[0] = Integer.valueOf(i);
    arrayOfObject[1] = Integer.valueOf(j);
    arrayOfObject[2] = Double.valueOf(d2);
    if(i >= 100){
    	return String.format("%03d°%02d′%02.3f″", arrayOfObject);
    }else{
    	return String.format("%02d°%02d′%02.3f″", arrayOfObject);
    }
    
  }

  public static void DoubleToByte(double paramDouble, byte[] paramArrayOfByte){
    double d1 = 60.0D * (60.0D * paramDouble);
    int i = (int)d1 / 60 / 60;
    int j = (int)(d1 / 60.0D - 60 * (int)(d1 / 60.0D / 60.0D));
    double d2 = d1 - 60 * (60 * (int)(d1 / 60.0D / 60.0D)) - 60 * (int)(d1 / 60.0D - 60 * (int)(d1 / 60.0D / 60.0D));
    int k = (int)d2;
    int m = (int)(10.0D * (d2 - k));
    paramArrayOfByte[0] = (byte)i;
    paramArrayOfByte[1] = (byte)j;
    paramArrayOfByte[2] = (byte)k;
    paramArrayOfByte[3] = (byte)m;
  }

  public static int GetCgfs(Context paramContext){
    return paramContext.getSharedPreferences("RDSS_SET", 0).getInt("CGFS", 0);
  }

  public static int GetDDG(Context paramContext){
    return paramContext.getSharedPreferences("RDSS_SET", 0).getInt("DDG", 0);
  }

  public static int GetFRQ(Context paramContext){
    return paramContext.getSharedPreferences("RDSS_SET", 0).getInt("FRQ", 60);
  }

  public static short GetInfoType4_1(Context paramContext, boolean paramBoolean1, boolean paramBoolean2){
    if (paramBoolean1);
    for (int i = 32; ; i = 0){
      int j = (short)i;
      int k = 0;
      if (paramBoolean2)
        k = 1;
      return (short)((short)(k + j) + (GetCgfs(paramContext) << 2));
    }
  }

  public static String GetSOSContent(Context paramContext)
  {
    return paramContext.getSharedPreferences("SOS_SET", 0).getString("SOS_CONTENT", null);
  }

  public static int GetSOSID(Context paramContext)
  {
    return paramContext.getSharedPreferences("SOS_SET", 0).getInt("SOS_SERVERID", 0);
  }
  
  public static int GetSOSFreq(Context paramContext)
  {
    return paramContext.getSharedPreferences("SOS_SET", 0).getInt("SOS_FREQ", 0);
  }

  public static int GetTXG(Context paramContext){
    return paramContext.getSharedPreferences("RDSS_SET", 0).getInt("TXG", 0);
  }
  
  public static int GetRDSSReportNumber(Context paramContext){
    return paramContext.getSharedPreferences("RDSS_REPORT", 0).getInt("BDNum", 0);
  }
  
  public static int GetRDSSReportFreq(Context paramContext){
    return paramContext.getSharedPreferences("RDSS_REPORT", 0).getInt("BDReportFreq", 0);
  }
  
  public static String GetRDSSReportContent(Context paramContext){
    return paramContext.getSharedPreferences("RDSS_REPORT", 0).getString("BDReportContent", "");
  }

  public static BDLocation LBToGOSS(double paramDouble1, double paramDouble2, double paramDouble3){
    return DD_GOSS(paramDouble1, paramDouble2, paramDouble3, 0.0D);
  }

  public static void SaveSOSData(Context paramContext, int number, String paramString, int freq){
    Editor localEditor = paramContext.getSharedPreferences("SOS_SET", 0).edit();
    localEditor.putInt("SOS_SERVERID", number);
    localEditor.putString("SOS_CONTENT", paramString);
    localEditor.putInt("SOS_FREQ", freq);
    localEditor.commit();
  }

  public static void SaveSetData(Context paramContext, int paramInt1, int paramInt2, int paramInt3, int paramInt4){
    Editor editor = paramContext.getSharedPreferences("RDSS_SET", Context.MODE_PRIVATE).edit();
    editor.putInt("FRQ", paramInt1);
    editor.putInt("DDG", paramInt2);
    editor.putInt("TXG", paramInt3);
    editor.putInt("CGFS", 1);
    editor.commit();
  }

  public static void SaveRDSSReportData(Context paramContext, int bdNum, int freq, String content){
    Editor editor = paramContext.getSharedPreferences("RDSS_REPORT", Context.MODE_PRIVATE).edit();
    editor.putInt("BDNum", bdNum);
    editor.putInt("BDReportFreq", freq);
    editor.putString("BDReportContent", content);
    editor.commit();
  }







  public static DisplayMetrics getMeterics()
  {
    return metriscs;
  }

    public static String Byte2Hex(Byte paramByte){
      return String.format("%02x", new Object[] { paramByte }).toUpperCase();
    }

    public static String ByteArrToHex(byte[] paramArrayOfByte){
      StringBuilder localStringBuilder = new StringBuilder();
      int i = paramArrayOfByte.length;
      for (int j = 0; j < i; j++){
        localStringBuilder.append(Byte2Hex(Byte.valueOf(paramArrayOfByte[j])));
        localStringBuilder.append(" ");
      }
      return localStringBuilder.toString();
    }

    public static String ByteArrToHex(byte[] paramArrayOfByte, int paramInt1, int paramInt2){
      StringBuilder localStringBuilder = new StringBuilder();
      for (int i = paramInt1; i < paramInt2; i++){
        localStringBuilder.append(Byte2Hex(Byte.valueOf(paramArrayOfByte[i])));
      }
      return localStringBuilder.toString();
    }

    public static byte HexToByte(String paramString){
      return (byte)Integer.parseInt(paramString, 16);
    }

    public static byte[] HexToByteArr(String paramString){
      int len = 0;
      int index = 0;
      if(paramString.length() % 2 == 0){
    	  len = paramString.length() / 2; 
      }else{
    	  len = paramString.length() / 2 + 1;
    	  paramString = "0" + paramString;
      }
      if(len == 0){
    	  return null;
      }
      byte[] hexBytes = new byte[len];
      for(int i = 0; i < paramString.length() - 2; i++){
    	  hexBytes[index] = HexToByte(paramString.substring(i, i + 2));
    	  index ++;
      }
      return hexBytes;
    }

    public static int HexToInt(String paramString){
      return Integer.parseInt(paramString, 16);
    }
    
    /**
     * 基于余弦定理求两经纬度距离
     * 
     * @param lon1
     *            第一点的精度
     * @param lat1
     *            第一点的纬度
     * @param lon2
     *            第二点的精度
     * @param lat3
     *            第二点的纬度
     * @return 返回的距离，单位m
     * */
    public static final double EARTH_RADIUS = 6378137;// 赤道半径(单位m)
    public static double LantitudeLongitudeDist(double lon1, double lat1,
    		double lon2, double lat2) {
    	double radLat1 = rad(lat1);
    	double radLat2 = rad(lat2);

    	double radLon1 = rad(lon1);
    	double radLon2 = rad(lon2);

    	if (radLat1 < 0)
    		radLat1 = Math.PI / 2 + Math.abs(radLat1);// south
    	if (radLat1 > 0)
    		radLat1 = Math.PI / 2 - Math.abs(radLat1);// north
    	if (radLon1 < 0)
    		radLon1 = Math.PI * 2 - Math.abs(radLon1);// west
    	if (radLat2 < 0)
    		radLat2 = Math.PI / 2 + Math.abs(radLat2);// south
    	if (radLat2 > 0)
    		radLat2 = Math.PI / 2 - Math.abs(radLat2);// north
    	if (radLon2 < 0)
    		radLon2 = Math.PI * 2 - Math.abs(radLon2);// west
    	double x1 = EARTH_RADIUS * Math.cos(radLon1) * Math.sin(radLat1);
    	double y1 = EARTH_RADIUS * Math.sin(radLon1) * Math.sin(radLat1);
    	double z1 = EARTH_RADIUS * Math.cos(radLat1);

    	double x2 = EARTH_RADIUS * Math.cos(radLon2) * Math.sin(radLat2);
    	double y2 = EARTH_RADIUS * Math.sin(radLon2) * Math.sin(radLat2);
    	double z2 = EARTH_RADIUS * Math.cos(radLat2);

    	double d = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)
    			+ (z1 - z2) * (z1 - z2));
    	// 余弦定理求夹角
    	double theta = Math.acos((EARTH_RADIUS * EARTH_RADIUS + EARTH_RADIUS
    			* EARTH_RADIUS - d * d)
    			/ (2 * EARTH_RADIUS * EARTH_RADIUS));
    	double dist = theta * EARTH_RADIUS;
    	return dist;
    }


    /**
     * 转化为弧度(rad)
     * */
    public static double rad(double d) {
    	return d * Math.PI / 180.0;
    }

    public static String bytesArrayToHex(byte[] content) {
      if (content == null || content.length < 1) {
        return null;
      }
      String result = "";
      for (int i = 0; i < content.length; i++) {
        int temp = content[i];
        if (temp < 0) {
          temp = 256 + temp;
        }
        result = result + String.format("%02x", new Object[]{content[i]}).toUpperCase() + " ";
      }
      return result;
    }
}
