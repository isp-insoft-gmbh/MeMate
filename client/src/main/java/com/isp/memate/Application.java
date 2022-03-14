package com.isp.memate;

import com.isp.memate.util.Compare;
import com.isp.memate.util.PropertyHelper;

import javafx.stage.Stage;

public class Application extends javafx.application.Application
{
  public static void main( String[] args )
  {
    launch( args );
  }

  @Override
  public void start( Stage primaryStage ) throws Exception
  {
    primaryStage.close();
    if ( PropertyHelper.isSessionIDValid() )
    {
      showMainframe();
    }
    else
    {
      showLogin();
    }
  }

  private static void showMainframe()
  {
    new MainFrame();
    Compare.checkVersion();
  }

  private static void showLogin()
  {
    new Login();
    Compare.checkVersion();
  }
}

