package printheadDetails.warrantyStatus;

import java.util.Comparator;

public class SortByPhSerialNoWithStatus implements Comparator<PrintheadDetailsResultDb>
{
    // Used for sorting in ascending order of ph name
    public int compare(PrintheadDetailsResultDb   a, PrintheadDetailsResultDb  b)
    {
        return a.getPh_serial_no().compareTo(b.getPh_serial_no());
    }
}