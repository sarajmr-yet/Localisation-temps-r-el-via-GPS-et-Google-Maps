<?php
interface ICrud {
    public function insert($obj);
    public function modify($obj);
    public function remove($obj);
    public function findById($id);
    public function fetchAll();
}
