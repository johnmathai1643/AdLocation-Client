package com.example.john.locationads;


import android.location.Location;

public class NodeManager {

    Double _dest_point_lon,_source_point_lat, _dest_point_lat, _source_point_lon;
    String _source_place, _dest_place;
    int _id;
    int _freq;
    long _source_time,_dest_time;

    public NodeManager(){

    }

    public NodeManager(int _id, Double _source_point_lat, Double _source_point_lon, Double _dest_point_lon, Double _dest_point_lat, int _freq, long _source_time, long _dest_time,String _source_place,String _dest_place){
        this._id = _id;
        this._source_point_lat = _source_point_lat;
        this._source_point_lon = _source_point_lon;
        this._dest_point_lon = _dest_point_lon;
        this._dest_point_lat = _dest_point_lat;
        this._freq = _freq;
        this._source_time = _source_time;
        this._dest_time = _dest_time;
        this._source_place = _source_place;
        this._dest_place = _dest_place;
    }

    public NodeManager(Double _source_point_lat, Double _source_point_lon, Double _dest_point_lon, Double _dest_point_lat, int _freq, long _source_time, long _dest_time,String _source_place,String _dest_place){
        this._source_point_lat = _source_point_lat;
        this._source_point_lon = _source_point_lon;
        this._dest_point_lon = _dest_point_lon;
        this._dest_point_lat = _dest_point_lat;
        this._freq = _freq;
        this._source_time = _source_time;
        this._dest_time = _dest_time;
        this._source_place = _source_place;
        this._dest_place = _dest_place;
    }

    public Double get_source_point_lat(){
        return this._source_point_lat;
    }

    public void set_source_point_lat(Double _source_point_lat){
        this._source_point_lat = _source_point_lat;
    }

    public Double get_source_point_lon(){
        return this._source_point_lon;
    }

    public void set_source_point_lon(Double _source_point_lon){
        this._source_point_lon = _source_point_lon;
    }

    public Double get_dest_point_lat(){
        return this._dest_point_lat;
    }

    public void set_dest_point_lat(Double _dest_point_lat){
        this._dest_point_lat = _dest_point_lat;
    }

    public Double get_dest_point_lon(){
        return this._dest_point_lon;
    }

    public void set_dest_point_lon(Double _dest_point_lon){
        this._dest_point_lon = _dest_point_lon;
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

    public void set_source_time(long _source_time){
       this._source_time = _source_time;
    }

    public long get_source_time(){
        return _source_time;
    }

    public void set_dest_time(long _dest_time){
        this._dest_time = _dest_time;
    }

    public long get_dest_time(){
        return _dest_time;
    }

    public void set_source_place(String _source_place){
        this._source_place = _source_place;
    }

    public String get_source_place(){
        return _source_place;
    }

    public void set_dest_place(String _dest_place){
        this._dest_place = _dest_place;
    }

    public String get_dest_place(){
        return _dest_place;
    }


    public Location get_source_node_location(){
        Location location = null;
        location.setLatitude(get_source_point_lat());
        location.setLongitude(get_source_point_lon());
        return location;
    }

    public Location get_dest_node_location(){
        Location location = null;
        location.setLatitude(get_dest_point_lat());
        location.setLongitude(get_dest_point_lon());
        return location;
    }

}
