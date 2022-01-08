package com.ecommerceapi.ecomerceapi.controllers;

import com.ecommerceapi.ecomerceapi.dto.request.FilterListRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Role.RoleFormRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseAnyDTO;
import com.ecommerceapi.ecomerceapi.dto.response.ResponseListAnyDTO;
import com.ecommerceapi.ecomerceapi.dto.response.Role.RoleResponseDTO;
import com.ecommerceapi.ecomerceapi.filters.AuthFilter;
import com.ecommerceapi.ecomerceapi.services.RoleService;
import com.ecommerceapi.ecomerceapi.util.ConstantUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/role")
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private AuthFilter authFilter;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    /** Create Role Post Method */
    @PostMapping()
    public ResponseEntity create(@Valid @RequestBody RoleFormRequestDTO roleFormRequestDTO, HttpServletRequest request) {

        /** Check Token */
        String token = authFilter.getToken(request);
        if (token == null) throw new ValidationException("Token is empty");

        /** Create Role */
        RoleResponseDTO roleResponseDTO = roleService.create(roleFormRequestDTO, token);

        /** Result Response */
        ResponseAnyDTO<RoleResponseDTO> responseAnyDTO = new ResponseAnyDTO<>(ConstantUtil.STATUS_SUCCESS,
                ConstantUtil.MESSAGE_SUCCESS, roleResponseDTO);

        logger.info("Role has Created");
        return new ResponseEntity<>(responseAnyDTO, HttpStatus.CREATED);
    }

    /** Update Role Put Method */
    @PutMapping()
    public ResponseEntity update(@Valid @RequestBody RoleFormRequestDTO roleFormRequestDTO, HttpServletRequest request) {

        /** Check Token */
        String token = authFilter.getToken(request);
        if (token == null) throw new ValidationException("Token is empty");

        /** Update Role */
        RoleResponseDTO roleResponseDTO = roleService.update(roleFormRequestDTO, token);

        /** Result Response */
        ResponseAnyDTO<RoleResponseDTO> responseAnyDTO = new ResponseAnyDTO<>(ConstantUtil.STATUS_SUCCESS,
                ConstantUtil.MESSAGE_SUCCESS, roleResponseDTO);

        logger.info("Role has Updated");
        return new ResponseEntity<>(responseAnyDTO, HttpStatus.CREATED);
    }

    /** Detail Role Get Method */
    @GetMapping("/{id}")
    public ResponseEntity view(HttpServletRequest request, @PathVariable("id") Integer id) {

        /** Check Token */
        String token = authFilter.getToken(request);
        if (token == null) throw new ValidationException("Token is empty");

        /** Check Id */
        if (!isExistingDataAndIntegerValue(id)) throw new ValidationException("Id must be number");

        /** Detail Role */
        RoleResponseDTO roleResponseDTO = roleService.view(id, token);

        /** Result Response */
        ResponseAnyDTO<RoleResponseDTO> responseAnyDTO = new ResponseAnyDTO<>(ConstantUtil.STATUS_SUCCESS,
                ConstantUtil.MESSAGE_SUCCESS, roleResponseDTO);

        logger.info("Role Detailed");
        return new ResponseEntity<>(responseAnyDTO, HttpStatus.OK);
    }

    /** List Role Get Method */
    @GetMapping()
    public ResponseEntity list(@Valid FilterListRequestDTO filterListRequestdto, HttpServletRequest request) {

        /** Check Token */
        String token = authFilter.getToken(request);
        if (token == null) throw new ValidationException("Token is empty");

        /** List Role */
        List<RoleResponseDTO> roleResponseDTOList = roleService.list(filterListRequestdto, token);
        Map detailList = roleService.totalPage(filterListRequestdto);

        /** Result Response */
        ResponseListAnyDTO<RoleResponseDTO> responseListAnyDTO = new ResponseListAnyDTO<>(ConstantUtil.STATUS_SUCCESS,
                ConstantUtil.MESSAGE_SUCCESS, detailList, roleResponseDTOList);

        logger.info("Role Listed");
        return new ResponseEntity<>(responseListAnyDTO, HttpStatus.OK);
    }

    /** Remove Role Delete Method */
    @DeleteMapping("/{id}")
    public ResponseEntity delete(HttpServletRequest request, @PathVariable Integer id) {

        /** Check Token */
        String token = authFilter.getToken(request);
        if (token == null) throw new ValidationException("Token is empty");

        /** Check Id */
        if (!isExistingDataAndIntegerValue(id)) throw new ValidationException("Id must be number");

        /** Delete Role */
        RoleResponseDTO roleResponseDTO = roleService.delete(id, token);

        /** Result Response */
        ResponseAnyDTO<RoleResponseDTO> responseAnyDTO = new ResponseAnyDTO<>(ConstantUtil.STATUS_SUCCESS,
                ConstantUtil.MESSAGE_SUCCESS, roleResponseDTO);

        logger.info("Role Removed");
        return new ResponseEntity<>(responseAnyDTO, HttpStatus.OK);
    }
}
