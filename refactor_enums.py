import os
import re

base_path = r'C:\Users\LENOVO1\Documents\PROJECT SKRIPSI\manajement_magang\src\main\java\com\bsi\manajement_magang'
enums_dir = os.path.join(base_path, 'enums')
os.makedirs(enums_dir, exist_ok=True)

enums = {
    'Gender': ['Laki-laki', 'Perempuan'],
    'StatusAbsensi': ['hadir', 'izin', 'sakit', 'alpha'],
    'StatusVerifikasi': ['PENDING', 'DISETUJUI', 'DITOLAK'],
    'StatusKegiatan': ['disetujui', 'belum disetujui', 'ditolak'],
    'StatusPeriode': ['aktif', 'selesai', 'batal']
}

for enum_name, values in enums.items():
    java_code = f'package com.bsi.manajement_magang.enums;\n\npublic enum {enum_name} {{\n'
    for val in values:
        enum_var = val.upper().replace('-', '_').replace(' ', '_')
        java_code += f'    {enum_var}("{val}"),\n'
    java_code = java_code.rstrip(',\n') + ';\n\n'
    java_code += '    private final String value;\n\n'
    java_code += f'    {enum_name}(String value) {{\n        this.value = value;\n    }}\n\n'
    java_code += '    public String getValue() {\n        return value;\n    }\n\n'
    
    java_code += f'    public static {enum_name} fromString(String text) {{\n'
    java_code += f'        for ({enum_name} b : {enum_name}.values()) {{\n'
    java_code += '            if (b.value.equalsIgnoreCase(text)) {\n                return b;\n            }\n        }\n'
    java_code += '        throw new IllegalArgumentException("No constant with text " + text + " found");\n    }\n'
    java_code += '}\n'
    
    with open(os.path.join(enums_dir, f'{enum_name}.java'), 'w') as f:
        f.write(java_code)

print('Enums created successfully.')
