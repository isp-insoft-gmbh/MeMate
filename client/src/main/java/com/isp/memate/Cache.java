package com.isp.memate;

import java.io.InputStream;
import java.util.Properties;

import com.isp.memate.util.ClientLog;

public class Cache
{
  private static final Cache instance      = new Cache();
  private String             serverVersion = null;
  private String             clientVersion = null;

  public Cache()
  {
    loadClientVersion();
  }

  private void loadClientVersion()
  {
    try ( InputStream input = Cache.class.getClassLoader().getResourceAsStream( "version.properties" ) )
    {
      final Properties versionProperties = new Properties();
      versionProperties.load( input );
      this.clientVersion = versionProperties.getProperty( "build_version" );
    }
    catch ( final Exception exception )
    {
      ClientLog.newLog( "Die version.properties konnten nicht geladen werden" );
      ClientLog.newLog( exception.getMessage() );
    }
    System.out.println( "Version des Clients: " + this.clientVersion );
  }


  public static Cache getInstance()
  {
    return instance;
  }

  public String getServerVersion()
  {
    return serverVersion;
  }

  public void setServerVersion( String serverVersion )
  {
    this.serverVersion = serverVersion;
  }

  public String getClientVersion()
  {
    return clientVersion;
  }
}
