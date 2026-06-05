#MENTOR#

# Query
1. status:  ("semua status", "sudah dinilai". "belum dinilai") 
2. nama mahasiswa: string

# Endpoint
1. penilaian
a. edit penilaian 
-endpoint ini digunakan untuk menilai mahasiswa
- mahasiswa hanya bisa mempunyai 1 nilai(one to one)
Statistik penilaian (support filter) 
a. total penilaian
b. total sudah dinilai
c. total belum di nilai 

#MAHASISWA#
# Query 
1. Status ("semua status", "sangat baik", "baik")
2. nama penilaian: string 
 
 # Endpoint 
 1. penilaian 
 a. cetak nilai(dalam bentuk pdf bisa di download)
 b. nilai akhir dan grade (mahasiswa bisa melihat nilai akhir dan grade saat sudah di nilai oleh mentor)