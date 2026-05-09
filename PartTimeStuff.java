package Ex99_R1CB03;

public class PartTimeStuff extends Stuff{
    
    public PartTimeStuff(int id, String name, int yearsService, int roleId, int shiftReq, int stuffTypeId){
        super(id, name, yearsService, roleId, shiftReq, stuffTypeId);
    }

    @Override
    public int getWorkHour(){
        return 20;
    }
}
