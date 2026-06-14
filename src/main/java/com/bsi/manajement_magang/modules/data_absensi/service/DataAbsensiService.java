package com.bsi.manajement_magang.modules.data_absensi.service;

import com.bsi.manajement_magang.modules.data_absensi.schema.response.AbsensiMahasiswaStatResponse;
import com.bsi.manajement_magang.modules.data_absensi.schema.response.AbsensiResponse;
import com.bsi.manajement_magang.modules.data_absensi.schema.response.AbsensiStatResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface DataAbsensiService {

    List<AbsensiResponse> listAbsensi(String status, String namaMahasiswa);

    AbsensiResponse verifyAbsensi(UUID id, String action);

    void deleteAbsensi(UUID id);

    AbsensiStatResponse getAbsensiStatistics(String namaMahasiswa);

    String getAttachmentUrl(UUID id);

    String exportRekapAbsensi(String status, String namaMahasiswa);

    AbsensiResponse submitAbsensi(UUID userId, String status, String keterangan, MultipartFile file);

    List<AbsensiResponse> getRiwayatAbsensi(UUID userId);

    AbsensiMahasiswaStatResponse getMahasiswaStat(UUID userId);

    Long getTotalKehadiran(UUID userId);
}
