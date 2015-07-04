package com.example.john.locationads;


public class NodeManager {

    Double _lat, _lng;
    String _place;
    int _id;
    int _freq;
    long _source_time,_dest_time;

    public NodeManager(){

    }

    public NodeManager(int _id, Double _lat, Double _lng, int _freq, String _place){
        this._id = _id;
        this._lng = _lng;
        this._lat = _lat;
        this._freq = _freq;
        this._place = _place;
    }

    public NodeManager(Double _lat, Double _lng, int _freq, String _place){
        this._lng = _lng;
        this._lat = _lat;
        this._freq = _freq;
        this._place = _place;
    }

    public Double get_lat(){
        return this._lat;
    }

    public void set_lat(Double _lat){
        this._lat = _lat;
    }

    public Double get_lng(){
        return this._lng;
    }

    public void set_lng(Double _lng){
        this._lng = _lng;
    }

    public void set_id(int _id){
        this._id = _id;
    }

    public int get_id(){ return this._id; }

    public void set_freq(int _freq){
        this._freq = _freq;
    }

    public int get_freq(){
        return this._freq;
    }

//
//    public void set_source_time(long _source_time){
//       this._source_time = _source_time;
//    }
//
//    public long get_source_time(){
//        return _source_time;
//    }
//
//    public void set_dest_time(long _dest_time){
//        this._dest_time = _dest_time;
//    }
//
//    public long get_dest_time(){
//        return _dest_time;
//    }

    public void set_place(String _place){
        this._place = _place;
    }

    public String get_place(){
        return _place;
    }

}
