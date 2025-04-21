package com.ld.springsecurity.repo;

import com.ld.springsecurity.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, String> {

}
