package com.ecommerceapi.ecomerceapi.mock;

import com.ecommerceapi.ecomerceapi.dto.request.FilterListRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Role.RoleFormRequestDTO;
import com.ecommerceapi.ecomerceapi.dto.response.Role.RoleResponseDTO;
import com.ecommerceapi.ecomerceapi.filters.AuthFilter;
import com.ecommerceapi.ecomerceapi.model.Admin;
import com.ecommerceapi.ecomerceapi.model.Role;
import com.ecommerceapi.ecomerceapi.repositories.RoleRepository;
import com.ecommerceapi.ecomerceapi.services.RoleService;
import com.ecommerceapi.ecomerceapi.services.impl.RoleServiceImplements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoleMockTest {
    String token;
    Role role;

    @Mock
    RoleRepository roleRepository;

    @Mock
    Role roleMock = new Role();

    @Mock
    AuthFilter authFilter;

    @InjectMocks
    RoleService roleService = new RoleServiceImplements();

    @BeforeEach
    void setupTest() {
        token = "TestToken";
        role = new Role();
    }

    @DisplayName("Test Mock Create a Role")
    @Test
    void testCreateRole() {
        RoleFormRequestDTO roleFormRequestDTO = new RoleFormRequestDTO();
        roleFormRequestDTO.setName("TestRole");

        when(authFilter.getAdminFromToken(token)).thenReturn(new Admin());
        when(roleRepository.findOneByNameActive(anyString())).thenReturn(null);
        when(roleMock.getName()).thenReturn(roleFormRequestDTO.getName());
        when(roleRepository.saveAndFlush(any(Role.class))).thenReturn(roleMock);

        RoleResponseDTO roleResponseDTO = roleService.create(roleFormRequestDTO, token);
        assertNotNull(roleResponseDTO);
        assertNotNull(roleResponseDTO.getId());
        assertEquals(roleFormRequestDTO.getName(), roleResponseDTO.getName());
        verify(roleRepository).findOneByNameActive(anyString());
        verify(roleRepository).saveAndFlush(any(Role.class));
    }

    @DisplayName("Test Mock Update a Role")
    @Test
    void testUpdateRole() {
        role.setId(14);
        role.setName("TestRole");
        RoleFormRequestDTO roleFormRequestDTO = new RoleFormRequestDTO();
        roleFormRequestDTO.setId(role.getId());
        roleFormRequestDTO.setName(role.getName());

        when(authFilter.getAdminFromToken(token)).thenReturn(new Admin());
        when(roleRepository.findOneById(roleFormRequestDTO.getId())).thenReturn(role);
        when(roleMock.getId()).thenReturn(roleFormRequestDTO.getId());
        when(roleMock.getName()).thenReturn(roleFormRequestDTO.getName());
        when(roleRepository.findOneByNameActiveEx(anyInt(), anyString())).thenReturn(null);
        when(roleRepository.saveAndFlush(any(Role.class))).thenReturn(roleMock);

        RoleResponseDTO roleResponseDTO = roleService.update(roleFormRequestDTO, token);
        assertNotNull(roleResponseDTO);
        assertNotNull(roleResponseDTO.getId());
        assertEquals(roleFormRequestDTO.getId(), roleResponseDTO.getId());
        assertEquals(roleFormRequestDTO.getName(), roleResponseDTO.getName());
        verify(roleRepository).findOneById(role.getId());
        verify(roleRepository).findOneByNameActiveEx(anyInt(), anyString());
        verify(roleRepository).saveAndFlush(any(Role.class));
    }

    @DisplayName("Test Mock View a Role")
    @Test
    void testViewRole() {
        role.setId(14);

        when(authFilter.getAdminFromToken(token)).thenReturn(new Admin());
        when(roleRepository.findOneById(role.getId())).thenReturn(role);

        RoleResponseDTO roleResponseDTO = roleService.view(role.getId(), token);
        assertNotNull(roleResponseDTO);
        assertNotNull(roleResponseDTO.getId());
        assertEquals(role.getId(), roleResponseDTO.getId());
        verify(roleRepository).findOneById(role.getId());
    }

    @DisplayName("Test Mock List Roles")
    @Test
    void testListRole() {
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(new Role());
        roles.add(new Role());

        FilterListRequestDTO filterListRequestDTO = new FilterListRequestDTO();
        filterListRequestDTO.setOffset(0);
        filterListRequestDTO.setLimit(2);
        filterListRequestDTO.setSearch("");

        when(authFilter.getAdminFromToken(token)).thenReturn(new Admin());
        when(roleRepository.findAllOffsetLimit(filterListRequestDTO.getSearch(), filterListRequestDTO.getLimit(),
                filterListRequestDTO.getOffset())).thenReturn(roles);

        List<RoleResponseDTO> listRoleResponseDTO = roleService.list(filterListRequestDTO, token);
        assertNotNull(listRoleResponseDTO);
        assertEquals(2, listRoleResponseDTO.size());
        verify(roleRepository).findAllOffsetLimit(filterListRequestDTO.getSearch(), filterListRequestDTO.getLimit(),
                filterListRequestDTO.getOffset());
    }

    @DisplayName("Test Mock Delete a Role")
    @Test
    void testDeleteRole() {
        role.setId(14);

        when(authFilter.getAdminFromToken(token)).thenReturn(new Admin());
        when(roleRepository.findOneById(role.getId())).thenReturn(role);
        when(roleRepository.save(any(Role.class))).thenReturn(roleMock);

        RoleResponseDTO roleResponseDTO = roleService.delete(role.getId(), token);
        assertNotNull(roleResponseDTO);
        assertNotNull(roleResponseDTO.getId());
        assertEquals(role.getId(), roleResponseDTO.getId());
        assertEquals(false, roleResponseDTO.getStatus());
        verify(roleRepository).findOneById(role.getId());
        verify(roleRepository).save(any(Role.class));
    }

    @DisplayName("Test Mock Total Page Roles")
    @Test
    void testTotalPageRole() {
        FilterListRequestDTO filterListRequestDTO = new FilterListRequestDTO();
        filterListRequestDTO.setOffset(0);
        filterListRequestDTO.setLimit(2);
        filterListRequestDTO.setSearch("");

        when(roleRepository.findAllCount(filterListRequestDTO.getSearch())).thenReturn(2);

        Map totalPage = roleService.totalPage(filterListRequestDTO);
        assertNotNull(totalPage);
        assertEquals(1, totalPage.get("totalPage"));
        verify(roleRepository).findAllCount(filterListRequestDTO.getSearch());
    }
}
