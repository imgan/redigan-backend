package com.ecommerceapi.ecomerceapi.services;

import com.ecommerceapi.ecomerceapi.dto.request.FilterListRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Role.RoleFormRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.response.Role.RoleResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface RoleService {

    RoleResponseDTO create(RoleFormRequestDTO roleFormRequestDTO, String token);

    RoleResponseDTO update(RoleFormRequestDTO roleFormRequestDTO, String token);

    RoleResponseDTO view(Integer id, String token);

    List<RoleResponseDTO> list(FilterListRequestDTO filterListRequestdto, String token);

    RoleResponseDTO delete(Integer id, String token);

    Map totalPage(FilterListRequestDTO filterListRequestdto);

}
