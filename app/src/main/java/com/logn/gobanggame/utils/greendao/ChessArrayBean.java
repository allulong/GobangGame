package com.logn.gobanggame.utils.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by long on 2017/6/19.
 */

@Entity
public class ChessArrayBean {
    @Id
    private long _id;
    private String chessWhites;
    private String chessBlacks;
    private boolean hasBackups;
    @Generated(hash = 1999013990)
    public ChessArrayBean(long _id, String chessWhites, String chessBlacks,
            boolean hasBackups) {
        this._id = _id;
        this.chessWhites = chessWhites;
        this.chessBlacks = chessBlacks;
        this.hasBackups = hasBackups;
    }
    @Generated(hash = 1709629855)
    public ChessArrayBean() {
    }
    public long get_id() {
        return this._id;
    }
    public void set_id(long _id) {
        this._id = _id;
    }
    public String getChessWhites() {
        return this.chessWhites;
    }
    public void setChessWhites(String chessWhites) {
        this.chessWhites = chessWhites;
    }
    public String getChessBlacks() {
        return this.chessBlacks;
    }
    public void setChessBlacks(String chessBlacks) {
        this.chessBlacks = chessBlacks;
    }
    public boolean getHasBackups() {
        return this.hasBackups;
    }
    public void setHasBackups(boolean hasBackups) {
        this.hasBackups = hasBackups;
    }
}
