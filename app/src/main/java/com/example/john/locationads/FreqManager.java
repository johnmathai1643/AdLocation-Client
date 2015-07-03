package com.example.john.locationads;


public class FreqManager {
    Double _start_point_lat;
    Double _start_point_lon;
    Double _end_point_lat;
    Double _end_point_lon;
    int _id;
    int _node_id;
    int _freq;

    public FreqManager(){

    }

    public FreqManager(int _id, int _node_id, Double _start_point_lat,Double _start_point_lon,Double _end_point_lon, Double _end_point_lat, int _freq){
        this._id = _id;
        this._node_id = _id;
        this._start_point_lat = _start_point_lat;
        this._start_point_lon = _start_point_lon;
        this._end_point_lon = _end_point_lon;
        this._end_point_lat = _end_point_lat;
        this._freq = _freq;
    }


    public FreqManager(int _node_id, Double _start_point_lat,Double _start_point_lon,Double _end_point_lon, Double _end_point_lat, int _freq){
        this._node_id = _id;
        this._start_point_lat = _start_point_lat;
        this._start_point_lon = _start_point_lon;
        this._end_point_lon = _end_point_lon;
        this._end_point_lat = _end_point_lat;
        this._freq = _freq;
    }


    public Double get_start_point_lat(){
        return this._start_point_lat;
    }

    public void set_start_point_lat(Double _start_point_lat){
        this._start_point_lat = _start_point_lat;
    }

    public Double get_start_point_lon(){
        return this._start_point_lon;
    }

    public void set_start_point_lon(Double _start_point_lon){
        this._start_point_lon = _start_point_lon;
    }

    public Double get_end_point_lat(){
        return this._end_point_lat;
    }

    public void set_end_point_lat(Double _end_point_lat){
        this._end_point_lat = _end_point_lat;
    }

    public Double get_end_point_lon(){
        return this._end_point_lon;
    }

    public void set_end_point_lon(Double _end_point_lon){
        this._end_point_lon = _end_point_lon;
    }

    public int get_id(){ return this._id; }

    public void set_id(int _id){
        this._id = _id;
    }

    public int get_node_id(){ return this._node_id; }

    public void set_node_id(int _node_id){
        this._node_id = _node_id;
    }

    public void set_freq(int _freq){
        this._freq = _freq;
    }

    public int get_freq(){
        return this._freq;
    }
}
