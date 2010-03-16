/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package game.server;

import java.util.Vector;

/**
 *
 * @author Sergio
 */
public class ComObject {
    private int code;
    private Vector objects;
    private String tag;

    public ComObject(int code){
        this.objects = new Vector();
        this.code = code;
    }
    
    public ComObject(int code, Vector objects){
        this.code = code;
        this.objects = objects;
    }
    
    public ComObject(int code, Vector objects, String tag){
        this.code = code;
        this.objects = objects;
        this.tag = tag;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Vector getObjects() {
        return objects;
    }

    public void setObjects(Vector objects) {
        this.objects = objects;
    }

    public void addObject(Object o){
        this.objects.add(o);
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }


}
