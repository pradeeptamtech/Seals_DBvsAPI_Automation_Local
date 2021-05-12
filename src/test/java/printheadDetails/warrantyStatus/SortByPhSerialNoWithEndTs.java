package printheadDetails.warrantyStatus;

import java.util.Comparator;

public class SortByPhSerialNoWithEndTs implements Comparator<PhDetailsApiEndTsResultDb>
{
    // Used for sorting in ascending order of ph name
    public int compare(PhDetailsApiEndTsResultDb   a, PhDetailsApiEndTsResultDb  b)
    {
        return a.getPh_serial_no().compareTo(b.getPh_serial_no());
    }
}
