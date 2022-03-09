package com.isp.memate;

import com.isp.memate.util.PropertyHelper;

/**
 * Initializes the folder structure and UIManager. It then checks whether there is a valid SessionID and
 * depending on this, the login or mainframe is established.
 *
 * @author nwe
 * @since 15.10.2019
 */
class Main
{
  public static void main( final String[] args )
  {
    Cache.getInstance().setDebugMode( args.length == 2 && "-debug".equals( args[ 0 ] ) && "true".equals( args[ 1 ] ) );
    ServerCommunication.getInstance();
    PropertyHelper.createPropFile();
    //    MeMateUIManager.init();

    Application.main( args );

  }
}