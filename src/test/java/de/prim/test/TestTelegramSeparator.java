package de.prim.test;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.prim.avilight.Constants;
import de.prim.avilight.utils.HexUtils;
import de.prim.comm.processor.DataProcessor;
import de.prim.comm.processor.TelegramSeparationProcessor;

@Test
public class TestTelegramSeparator
{
  private class TelegramSeparatorTester implements DataProcessor
  {
    private byte[]                      lastTelegram;
    private int                         nextIndex;
    private byte[][]                    expectNext;

    private TelegramSeparationProcessor toTest;

    public TelegramSeparatorTester()
    {
      super();
      this.toTest = new TelegramSeparationProcessor( this, Constants.BUFFER_SIZE );
    }

    public void testProcessData( byte[] buffer, int length )
    {
      toTest.processData( buffer, length );
    }

    public void setExpectNext( byte[][] expectNext )
    {
      this.expectNext = expectNext;
      nextIndex = 0;
      lastTelegram = null;
    }

    public byte[] getLastTelegram()
    {
      return lastTelegram;
    }

    public int getNextIndex()
    {
      return nextIndex;
    }

    @Override
    public void processData( byte[] buffer, int length )
    {
      if ( expectNext == null )
      {
        Assert.assertTrue( false, "Nothing expected" );
      }
      else
      {
        Assert.assertTrue( nextIndex < expectNext.length, "To much received" );
        byte next[] = expectNext[nextIndex++];

        Assert.assertEquals( length, next.length, "Expected: " + HexUtils.toHex( next )
            + " Received: " + HexUtils.toHex( buffer, length ) );

        boolean equals = true;
        for ( int i = 0; i < length; i++ )
        {
          if ( buffer[i] != next[i] )
          {
            equals = false;
            break;
          }
        }
        Assert
            .assertTrue(
                equals,
                "Expected: " + HexUtils.toHex( next ) + " Received: "
                    + HexUtils.toHex( buffer, length ) );
      }
      lastTelegram = buffer;
    }

  };

  public void testSimpleTelegram()
  {
    TelegramSeparatorTester tester = new TelegramSeparatorTester();

    for ( int b = 0; b < 255; b++ )
    {
      byte[] send;
      byte[][] expected;
      if ( ( b == 27 ) || ( b == 3 ) )
      {
        // Escape
        send = new byte[]
        { 2, 27, (byte) b, 3 };
        expected = new byte[][]
        {
        { (byte) b } };
      }
      else
      {
        send = new byte[]
        { 2, (byte) b, 3 };
        expected = new byte[][]
        {
        { (byte) b } };
        ;
      }

      tester.setExpectNext( expected );
      tester.testProcessData( send, send.length );
      Assert.assertNotNull( tester.getLastTelegram(), "Nothing received" );
    }
  }

  public void testSplittedTelegram()
  {
    TelegramSeparatorTester tester = new TelegramSeparatorTester();

    tester.testProcessData( new byte[]
    { 2, 27 }, 2 );
    tester.setExpectNext( new byte[][]
    {
    { 3 } } );
    tester.testProcessData( new byte[]
    { 3, 3 }, 2 );

    tester.setExpectNext( null );
    tester.testProcessData( new byte[]
    { 2 }, 1 );
    tester.testProcessData( new byte[]
    { 25 }, 1 );
    tester.setExpectNext( new byte[][]
    {
    { 25 } } );
    tester.testProcessData( new byte[]
    { 3 }, 1 );
    Assert.assertNotNull( tester.getLastTelegram(), "Nothing received" );
  }

  public void testMultipleTelegram()
  {
    TelegramSeparatorTester tester = new TelegramSeparatorTester();

    tester.setExpectNext( new byte[][]
    {
    { 7 },
    { 8 } } );
    tester.testProcessData( new byte[]
    { 2, 7, 3, 2, 8, 3 }, 6 );
    Assert.assertEquals( tester.getNextIndex(), 2, "Telegram count mismatch" );

    tester.setExpectNext( new byte[][]
    {
    { 127, (byte) 128, (byte) 129 },
    { 2, 3, 27, 5 } } );
    tester.testProcessData( new byte[]
    { 2, 127, (byte) 128, (byte) 129, 3, 2 }, 6 );
    tester.testProcessData( new byte[]
    { 2, 27, 3, 27, 27, 5, 3 }, 7 );
    Assert.assertEquals( tester.getNextIndex(), 2, "Telegram count mismatch" );
  }
}
