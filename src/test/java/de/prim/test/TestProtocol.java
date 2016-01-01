package de.prim.test;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.prim.comm.event.CommEvent;
import de.prim.comm.event.CommEventChannelInfo;
import de.prim.comm.event.CommEventListener;
import de.prim.comm.event.CommEventPing;
import de.prim.comm.event.CommEventText;
import de.prim.comm.protocol.AviLightProtocol;

@Test
public class TestProtocol
{
  CommEvent testSequence( byte[] buffer, final Class<?>[] expectedAnswer )
  {
    AviLightProtocol aviLightProtocol = new AviLightProtocol( new CommEventListener()
    {
      int index = 0;

      @Override
      public void eventOccured( CommEvent commEvent )
      {
        if ( index > expectedAnswer.length )
        {
          Assert.fail( "Too much events." );
        }

        if ( index < expectedAnswer.length )
        {
          Class<?> expectedClass = expectedAnswer[index++];

          Assert.assertEquals( expectedClass, commEvent.getClass() );
        }
      }
    } );

    aviLightProtocol.processData( buffer, buffer.length );

    return aviLightProtocol.getLastEvent();
  }

  @Test ( description = "test a simple Telegram")
  void testSimpleTelegram()
  {
    testSequence( new byte[]
    { 2, 0, 3 }, new Class<?>[]
    { CommEventPing.class } );
  }

  @Test ( description = "Test synchronisation")
  void testsync()
  {
    testSequence( new byte[]
    { 5, 3, 2, 0, 3, 7, 2, 0, 3 }, new Class<?>[]
    { CommEventPing.class, CommEventPing.class } );
  }

  @Test ( description = "Test splitted telegrams")
  void testSplittedTelegrams()
  {
    testSequence( new byte[]
    { 2, 0, 3, 2 }, new Class<?>[]
    { CommEventPing.class } );
    testSequence( new byte[]
    { 0, 3 }, new Class<?>[]
    { CommEventPing.class } );
  }

  @Test ( description = "Test Info Telegram")
  void testInfo()
  {
    CommEventText commEventText = (CommEventText) testSequence( new byte[]
    { 2, 1, 'H', 'a', 'l', 'l', 'o', 3 }, new Class<?>[]
    { CommEventText.class } );

    Assert.assertEquals( 1, commEventText.getSource() );
    Assert.assertEquals( "Hallo", commEventText.getText() );
  }

  @Test ( description = "Test Terminal Telegram")
  void testTerminal()
  {
    CommEventText commEventText = (CommEventText) testSequence( new byte[]
    { 2, 2, 'T', 'e', 'r', 'm', 'i', 'n', 'a', 'l', 3 }, new Class<?>[]
    { CommEventText.class } );

    Assert.assertEquals( 2, commEventText.getSource() );
    Assert.assertEquals( "Terminal", commEventText.getText() );
  }

  @Test ( description = "Test Escape Sequence")
  void testEscapeSequence()
  {
    CommEventChannelInfo commEventChannelInfo = (CommEventChannelInfo) testSequence( new byte[]
    { 2, 27, 3, 1, 2, 27, 3, 3 }, new Class<?>[]
    { CommEventChannelInfo.class } );

    Assert.assertEquals( 3, commEventChannelInfo.getSource() );
    Assert.assertEquals( 1, commEventChannelInfo.getOutputChannels() );
    Assert.assertEquals( 2, commEventChannelInfo.getSwitchChannels() );
    Assert.assertEquals( 3, commEventChannelInfo.getReceiverChannels() );
  }

  // @Test(description = "Test Escape Sequence splitted")
  // void testEscapeSequenceSplitted()
  // {
  // AviLightProtocol aviLightProtocol = new AviLightProtocol();
  //
  // continueTestSequence( aviLightProtocol, new byte[] { 2, 0, 3, 2, 27 },
  // new Class<?>[] { CommEventPing.class } );
  // aviLightProtocol.removeAllListener();
  // continueTestSequence( aviLightProtocol, new byte[] { 3, 8, 9, 27 },
  // new Class<?>[] {} );
  // aviLightProtocol.removeAllListener();
  // continueTestSequence( aviLightProtocol, new byte[] { 3, 3 },
  // new Class<?>[] { CommEventChannelInfo.class } );
  //
  // CommEventChannelInfo commEventChannelInfo = (CommEventChannelInfo)
  // aviLightProtocol
  // .getLastEvent();
  //
  // Assert.assertEquals( 3, commEventChannelInfo.getSource() );
  // Assert.assertEquals( 8, commEventChannelInfo.getOutputChannels() );
  // Assert.assertEquals( 9, commEventChannelInfo.getSwitchChannels() );
  // Assert.assertEquals( 3, commEventChannelInfo.getReceiverChannels() );
  // }
}
