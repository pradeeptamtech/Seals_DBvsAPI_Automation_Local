package printheadDetails.warrantyStatus;

import java.util.Comparator;

public class SortByPhSerialNoWithStartTs implements Comparator<PhDetailsApiStartTsResultDb>
{
    // Used for sorting in ascending order of ph name
    public int compare(PhDetailsApiStartTsResultDb   a, PhDetailsApiStartTsResultDb  b)
    {
        return a.getPh_serial_no().compareTo(b.getPh_serial_no());
    }
}
