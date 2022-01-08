package com.ecommerceapi.ecomerceapi.services.impl;

import com.ecommerceapi.ecomerceapi.dto.request.FilterListRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Role.RoleFormRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.response.Role.RoleResponseDTO;
import com.ecommerceapi.ecomerceapi.exception.ResultNotFoundException;
import com.ecommerceapi.ecomerceapi.exception.ResultServiceException;
import com.ecommerceapi.ecomerceapi.filters.AuthFilter;
import com.ecommerceapi.ecomerceapi.model.Admin;
import com.ecommerceapi.ecomerceapi.model.Role;
import com.ecommerceapi.ecomerceapi.repositories.RoleRepository;
import com.ecommerceapi.ecomerceapi.services.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.util.*;

@Component
public class RoleServiceImplements extends BaseServices implements RoleService {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    AuthFilter authFilter;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    /** Create Role */
    @Override
    @Transactional
    public RoleResponseDTO create(RoleFormRequestDTO roleFormRequestDTO, String token) {

        /** Initialize */
        RoleResponseDTO roleResponseDTO = new RoleResponseDTO();
        Role role = new Role();

        /** Check Admin Exist */
        Admin admin = authFilter.getAdminFromToken(token);
        if (admin == null) throw new ResultNotFoundException("Admin is not found");

        /** Check Role Exist */
        Role roleRepo = roleRepository.findOneByNameActive(roleFormRequestDTO.getName());
        if (roleRepo != null) throw new ValidationException("Role already");

        try {
            /** Save Role */
            role.setName(roleFormRequestDTO.getName());
            role.setStatus(true);
            role.setCreatedDate(new Date());
            Role roleSave = roleRepository.saveAndFlush(role);

            /** Role Object Result */
            roleResponseDTO.setId(roleSave.getId());
            roleResponseDTO.setName(roleSave.getName());
            roleResponseDTO.setStatus(roleSave.getStatus());

            return roleResponseDTO;
        } catch (Exception e) {
            logger.error("[FATAL]" + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** Update Role */
    @Override
    @Transactional
    public RoleResponseDTO update(RoleFormRequestDTO roleFormRequestDTO, String token) {

        /** Initialize */
        RoleResponseDTO roleResponseDTO = new RoleResponseDTO();

        /** Check Admin Exist */
        Admin admin = authFilter.getAdminFromToken(token);
        if (admin == null) throw new ResultNotFoundException("Admin is not found");

        /** Check Role Exist */
        Role role = roleRepository.findOneById(roleFormRequestDTO.getId());
        if (role == null) throw new ResultNotFoundException("Role is not found");

        /** Check Role Name Exist */
        Role roleRepo = roleRepository.findOneByNameActiveEx(roleFormRequestDTO.getId(), roleFormRequestDTO.getName());
        if (roleRepo != null) throw new ValidationException("Role name can't be the same");

        try {
            /** Update Role */
            role.setName(roleFormRequestDTO.getName());
            role.setUpdatedDate(new Date());
            Role roleSave = roleRepository.saveAndFlush(role);

            /** Role Object Result */
            roleResponseDTO.setId(roleSave.getId());
            roleResponseDTO.setName(roleSave.getName());
            roleResponseDTO.setStatus(roleSave.getStatus());

            return roleResponseDTO;
        } catch (Exception e) {
            logger.error("[FATAL]" + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** Detail Role */
    @Override
    public RoleResponseDTO view(Integer id, String token) {

        /** Initialize */
        RoleResponseDTO roleResponseDTO = new RoleResponseDTO();

        /** Check Admin Exist */
        Admin admin = authFilter.getAdminFromToken(token);
        if (admin == null) throw new ResultNotFoundException("Admin is not found");

        /** Check Role Exist */
        Role role = roleRepository.findOneById(id);
        if (role == null) throw new ResultNotFoundException("Role is not found");

        try {
            /** Role Object Result */
            roleResponseDTO.setId(role.getId());
            roleResponseDTO.setName(role.getName());
            roleResponseDTO.setStatus(role.getStatus());

            return roleResponseDTO;
        } catch (Exception e) {
            logger.error("[FATAL]" + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** List Role */
    @Override
    public List<RoleResponseDTO> list(FilterListRequestDTO filterListRequestDTO, String token) {

        /** Initialize */
        List<RoleResponseDTO> listRoleResponseDTO = new ArrayList<>();

        /** Check Admin Exist */
        Admin admin = authFilter.getAdminFromToken(token);
        if (admin == null) throw new ResultNotFoundException("Admin is not found");

        try{
            /** Find Roles */
            List<Role> listRole = roleRepository.findAllOffsetLimit(filterListRequestDTO.getSearch(),
                    filterListRequestDTO.getLimit(), filterListRequestDTO.getOffset());

            /** Role List Result */
            for (Role role : listRole) {
                RoleResponseDTO roleResponseDTO = new RoleResponseDTO();
                roleResponseDTO.setId(role.getId());
                roleResponseDTO.setName(role.getName());
                roleResponseDTO.setStatus(role.getStatus());
                listRoleResponseDTO.add(roleResponseDTO);
            }

            return listRoleResponseDTO;
        } catch (Exception e) {
            logger.error("[FATAL]" + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** Delete Role */
    @Override
    public RoleResponseDTO delete(Integer id, String token) {

        /** Initialize */
        RoleResponseDTO roleResponseDTO = new RoleResponseDTO();

        /** Check Admin Exist */
        Admin admin = authFilter.getAdminFromToken(token);
        if (admin == null) throw new ResultNotFoundException("Admin is not found");

        /** Check Role Exist */
        Role role = roleRepository.findOneById(id);
        if (role == null) throw new ResultNotFoundException("Role is not found");

        try {
            /** Role Object Result */
            roleResponseDTO.setId(role.getId());
            roleResponseDTO.setName(role.getName());
            roleResponseDTO.setStatus(false);

            /** Delete Role with Change Status */
            role.setStatus(false);
            roleRepository.save(role);

            return roleResponseDTO;
        } catch (Exception e) {
            logger.error("[FATAL]" + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

    /** Total Page Role */
    @Override
    public Map totalPage(FilterListRequestDTO filterListRequestDTO) {

        /** Initialize */
        Map data = new HashMap();

        try{
            /** Find Total Roles */
            Integer countListRole = roleRepository.findAllCount(filterListRequestDTO.getSearch());

            data.put("limit", filterListRequestDTO.getLimit());
            data.put("total", countListRole);
            data.put("totalPage", (int) Math.ceil((double) countListRole / filterListRequestDTO.getLimit()));

            return data;
        } catch (Exception e) {
            logger.error("[FATAL]" + e);
            throw new ResultServiceException(e.getMessage());
        }
    }

}
