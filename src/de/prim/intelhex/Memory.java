package de.prim.intelhex;

public class Memory
{
  public static final int PAGESIZE = 128;
  public static final int PAGES = 256; // ATMega328

  private byte[][] pages = new byte[PAGES][];

  public void set(int address, byte value)
  {
    if (address > PAGES * PAGESIZE)
    {
      throw new IllegalArgumentException( "illegal Memory address :" + address );
    }

    byte[] page = getOrCreatePage( address / PAGESIZE );

    page[address % PAGESIZE] = value;
  }

  public byte[] getPage(int pageNo)
  {
    if (pageNo > PAGES)
    {
      throw new IllegalArgumentException( "Illegal Page No: " + pageNo );
    }

    return pages[pageNo];
  }

  public byte[] getOrCreatePage(int pageNo)
  {
    byte[] page = getPage( pageNo );

    if (page == null)
    {
      page = new byte[PAGESIZE];
      pages[pageNo] = page;
    }

    return page;
  }

  public int getMaxPage()
  {
    for (int i = PAGES - 1; i > 0; i--)
    {
      if (getPage( i ) != null)
      {
        return i;
      }
    }

    return 0;
  }
}
