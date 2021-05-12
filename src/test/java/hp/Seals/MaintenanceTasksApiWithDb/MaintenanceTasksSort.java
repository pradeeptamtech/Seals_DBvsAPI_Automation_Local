package hp.Seals.MaintenanceTasksApiWithDb;

import java.util.Comparator;

public class MaintenanceTasksSort implements Comparator<List_maintenancesPojo>
{
    public int compare(List_maintenancesPojo   a, List_maintenancesPojo  b)
    {
        return a.getId().compareTo(b.getId());
    }
}