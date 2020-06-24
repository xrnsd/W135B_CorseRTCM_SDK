package com.kuyou.rtcm.utils;

public class BDLocation
{
  private double eartheight = 0.0D;
  private double lat = 0.0D;
  private double lon = 0.0D;

  public double getEarthHeight()
  {
    return this.eartheight;
  }

  public double getLatitude()
  {
    return this.lat;
  }

  public double getLongitude()
  {
    return this.lon;
  }

  public void setEarthHeight(double paramDouble)
  {
    this.eartheight = paramDouble;
  }

  public void setLatitude(double paramDouble)
  {
    this.lat = paramDouble;
  }

  public void setLongitude(double paramDouble)
  {
    this.lon = paramDouble;
  }
}
