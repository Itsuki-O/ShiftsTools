package Ex99_R1CB03;

public abstract class Stuff {
    private int id;
    private String name;
    private int yearsService;
    private int roleId;
    private int shiftReq;
    private int stuffTypeId;

    public Stuff(int id, String name, int yearsService, int roleId, int shiftReq, int stuffTypeId){
        this.id = id;
        setName(name);
        setYearsService(yearsService);
        setRoleId(roleId);
        setShiftReq(shiftReq);
        setStuffTypeId(stuffTypeId);
    }
    
    public abstract int getWorkHour();

// setter
    public void setName(String name) {
        this.name = name;
    }
    public void setYearsService(int yearsService) {
        this.yearsService = yearsService;
    }
    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }
    public void setShiftReq(int shiftReq) {
        this.shiftReq = shiftReq;
    }
    public void setStuffTypeId(int stuffTypeId) {
        this.stuffTypeId = stuffTypeId;
    }

// getter
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public int getRoleId() {
        return roleId;
    }
    public int getShiftReq() {
        return shiftReq;
    }
    public int getYearsService() {
        return yearsService;
    }
    public int getStuffTypeId() {
        return stuffTypeId;
    }
}
