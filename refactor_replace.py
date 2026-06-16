import os
import re

base_path = r'C:\Users\LENOVO1\Documents\PROJECT SKRIPSI\manajement_magang\src\main\java\com\bsi\manajement_magang'

replacements = [
    (r'\bString gender\b', 'Gender gender'),
    (r'\bString statusAbsensi\b', 'StatusAbsensi statusAbsensi'),
    (r'\bString statusVerifikasi\b', 'StatusVerifikasi statusVerifikasi'),
    (r'\bString statusKegiatan\b', 'StatusKegiatan statusKegiatan'),
    (r'\bString statusPeriode\b', 'StatusPeriode statusPeriode')
]

# Specifically we also have "String status" which could refer to different things depending on the module.
# In data_absensi, status -> StatusAbsensi
# In data_kegiatan, status -> StatusKegiatan
# In periode_magang / data_mahasiswa, status -> StatusPeriode

module_status_map = {
    'data_absensi': 'StatusAbsensi',
    'data_kegiatan': 'StatusKegiatan',
    'data_mahasiswa': 'StatusPeriode',
    'dashboard_mahasiswa': 'StatusPeriode',
    'dashboard_mentor': 'StatusPeriode'
}

for root, dirs, files in os.walk(base_path):
    if 'enums' in root:
        continue
    for file in files:
        if file.endswith('.java'):
            file_path = os.path.join(root, file)
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()

            orig_content = content
            
            # Global replacements
            for pattern, repl in replacements:
                content = re.sub(pattern, repl, content)

            # Module specific status replacements
            for module, enum_class in module_status_map.items():
                if f'\\modules\\{module}\\' in file_path or f'/modules/{module}/' in file_path.replace('\\', '/'):
                    content = re.sub(r'\bString status\b', f'{enum_class} status', content)
            
            # If any replacement happened, add imports if not present
            if content != orig_content:
                # Add imports
                enums_used = set()
                for e in ['Gender', 'StatusAbsensi', 'StatusVerifikasi', 'StatusKegiatan', 'StatusPeriode']:
                    if re.search(rf'\b{e}\b', content):
                        enums_used.add(e)
                
                if enums_used:
                    imports = '\n'.join([f'import com.bsi.manajement_magang.enums.{e};' for e in enums_used])
                    # Insert imports after the package declaration
                    content = re.sub(r'(package\s+[^;]+;)', r'\1\n\n' + imports, content, count=1)

                with open(file_path, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f'Updated {file_path}')
