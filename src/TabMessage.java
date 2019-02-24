import com.github.steveice10.mc.protocol.data.game.PlayerListEntry;
import com.github.steveice10.mc.protocol.data.message.Message;
import com.sasha.reminecraft.client.ReClient;

import java.util.List;

public class TabMessage {


    public String separator = ", ";

    public Message tabHeader, tabFooter;
    public List<PlayerListEntry> playerList;

    public TabMessage(){
        this.tabHeader = ReClient.ReClientCache.INSTANCE.tabHeader;
        this.tabFooter = ReClient.ReClientCache.INSTANCE.tabFooter;
        this.playerList = ReClient.ReClientCache.INSTANCE.playerListEntries;

    }


    public String getQueuePos() {
        String rV;
        try{
            rV = ReClient.ReClientCache.INSTANCE.tabHeader.getFullText().split("\n")[5].split(": ")[1].substring(2);
        } catch (Exception e){ rV = null; }
        return rV;
    }

    public String getQueueEstimatedTime(){
        String rV;
        try{
            rV = ReClient.ReClientCache.INSTANCE.tabHeader.getFullText().split("\n")[6].split(": ")[1].substring(2);
        } catch(Exception e){
            rV = null;
        }
        return rV;
    }


    @Override
    public boolean equals(Object obj) {
        // TODO
        return super.equals(obj);
    }



    public String toQueueString(){
        return this.getQueuePos() + this.separator + this.getQueueEstimatedTime();
    }
}
