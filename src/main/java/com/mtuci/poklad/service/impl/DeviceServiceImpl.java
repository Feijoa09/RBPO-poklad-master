package com.mtuci.poklad.service.impl;

import com.mtuci.poklad.models.ApplicationUser;
import com.mtuci.poklad.models.Device;
import com.mtuci.poklad.repositories.DeviceRepository;
import com.mtuci.poklad.requests.DataDeviceRequest;
import com.mtuci.poklad.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {
    private final DeviceRepository deviceRepository;
    private final UserServiceImpl userServiceImpl;

    private Device createDevice(String nameDevice, String macDevice, ApplicationUser user) {
        Device device = new Device();
        device.setName(nameDevice);
        device.setMacAddress(macDevice);
        device.setUser(user);
        return deviceRepository.save(device);
    }

    @Override
    public Device registerOrUpdateDevice(String nameDevice, String macDevice, ApplicationUser user) {
        Device device = deviceRepository.findByMacAddress(macDevice).orElse(null);

        // новое устройство у пользователя
        if (device == null || !device.getUser().getId().equals(user.getId())) {
            device = new Device();
            device.setName(nameDevice);
            device.setMacAddress(macDevice);
            device.setUser(user);
        }
        else if (!nameDevice.equals(device.getName())) {
            device.setName(nameDevice);
        }

        return deviceRepository.save(device);
    }

    @Override
    public Optional<Device> findDeviceByInfo(String name, String mac_address, ApplicationUser user) {
        return deviceRepository.findByNameAndMacAddressAndUser(name, mac_address, user);
    }

    @Override
    public Optional<Device> findDeviceById(Long id) {
        return deviceRepository.findById(id);
    }

    private Device edit(Device device, DataDeviceRequest deviceRequest) {
        device.setName(deviceRequest.getName());
        device.setMacAddress(deviceRequest.getMacAddress());
        device.setUser(userServiceImpl.getUserById(deviceRequest.getUserId()).orElseThrow(
                () -> new RuntimeException("Пользователь не найден")
        ));
        return device;
    }

    @Override
    public Device save(DataDeviceRequest request) {
        Device device = new Device();
        device.setName(request.getName());
        device.setMacAddress(request.getMacAddress());

        // Назначаем пользователя
        ApplicationUser user = userServiceImpl.getUserById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        device.setUser(user);

        return deviceRepository.save(device);
    }


    @Override
    public List<Device> getAll() {
        return deviceRepository.findAll();
    }

    @Override
    public Device update(DataDeviceRequest deviceRequest) {
        Device device = deviceRepository.findById(deviceRequest.getId()).orElseThrow(
                () -> new RuntimeException("Устройство не найдено")
        );
        return deviceRepository.save(edit(device, deviceRequest));
    }

    @Override
    public void delete(Long id) {
        deviceRepository.deleteById(id);
    }
}
