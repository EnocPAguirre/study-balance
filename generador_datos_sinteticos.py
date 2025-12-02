import pandas as pd
import numpy as np
from sklearn.preprocessing import LabelEncoder
import time

# ================== CONFIGURACIÓN ==================
INPUT_CSV = "student_lifestyle_dataset_synthetic_realistic.csv" # Tu archivo original
OUTPUT_CSV = "student_lifestyle_110M_final.csv"                 # El archivo gigante
TARGET_NEW_ROWS = 110_000_000                                   # Cuántas filas NUEVAS generar
CHUNK_SIZE = 500_000                                            # Filas por bloque (ajustar según RAM)

def main():
    start_time = time.time()
    print(f"--- Iniciando Generador Masivo (Original + Sintético) ---")
    
    # 1. CARGA Y ANÁLISIS DEL DATASET SEMILLA
    print(f"1. Leyendo dataset original: {INPUT_CSV}...")
    df_seed = pd.read_csv(INPUT_CSV)
    
    # Aseguramos el orden de columnas para que todo coincida
    columnas_ordenadas = df_seed.columns.tolist()
    
    # --- PREPARACIÓN MATEMÁTICA ---
    # Variables de tiempo que suman 24h (Independientes)
    # Physical_Activity se excluye porque será la variable "residual" (dependiente)
    time_vars_independent = [
        'Study_Hours_Per_Day', 
        'Extracurricular_Hours_Per_Day', 
        'Sleep_Hours_Per_Day', 
        'Social_Hours_Per_Day'
    ]
    
    # Otras numéricas
    cols_numericas = df_seed.select_dtypes(include=[np.number]).columns.tolist()
    other_numerics = [c for c in cols_numericas if c not in time_vars_independent 
                      and c != 'Physical_Activity_Hours_Per_Day' 
                      and c != 'Student_ID']
    
    # Categorías (excluyendo Stress_Level que es calculado)
    cols_categoricas = df_seed.select_dtypes(exclude=[np.number]).columns.tolist()
    if 'Stress_Level' in cols_categoricas: cols_categoricas.remove('Stress_Level')
    
    # Encoding para capturar correlaciones categóricas
    encoders = {}
    df_proc = df_seed.copy()
    for col in cols_categoricas:
        le = LabelEncoder()
        df_proc[col] = le.fit_transform(df_proc[col].astype(str))
        encoders[col] = le

    # Definir variables a modelar estadísticamente
    cols_a_modelar = time_vars_independent + other_numerics + cols_categoricas
    
    # Calcular Matriz de Covarianza y Medias
    mu = df_proc[cols_a_modelar].mean().values
    sigma = df_proc[cols_a_modelar].cov().values
    
    # 2. ESCRIBIR EL DATASET ORIGINAL AL INICIO
    print(f"2. Escribiendo datos originales al inicio de {OUTPUT_CSV}...")
    # index=False evita guardar el índice de pandas (0,1,2...)
    df_seed[columnas_ordenadas].to_csv(OUTPUT_CSV, index=False)
    
    print("3. Iniciando generación de datos sintéticos...")

    # 3. BUCLE DE GENERACIÓN MASIVA
    rows_generated = 0
    chunk_counter = 0
    # El ID sintético comienza donde termina el original
    current_id = df_seed['Student_ID'].max() + 1
    
    while rows_generated < TARGET_NEW_ROWS:
        # Calcular tamaño del bloque actual
        current_chunk_size = min(CHUNK_SIZE, TARGET_NEW_ROWS - rows_generated)
        
        # A. Generación Estadística (Multivariate Gaussian)
        datos_raw = np.random.multivariate_normal(mu, sigma, size=current_chunk_size)
        df_chunk = pd.DataFrame(datos_raw, columns=cols_a_modelar)
        
        # B. Lógica de Tiempo (Suma 24h)
        # 1. Evitar negativos iniciales en las variables base
        for col in time_vars_independent:
            df_chunk[col] = df_chunk[col].clip(lower=0)
            
        # 2. Calcular suma actual
        df_chunk['Sum_Hours'] = df_chunk[time_vars_independent].sum(axis=1)
        
        # 3. Corregir desbordamientos (> 24h)
        mask_overflow = df_chunk['Sum_Hours'] > 24
        if mask_overflow.any():
            factor = 24 / df_chunk.loc[mask_overflow, 'Sum_Hours']
            for col in time_vars_independent:
                df_chunk.loc[mask_overflow, col] *= factor
            # Si sumaban >24, al reducir a 24, queda 0 para actividad física
            df_chunk.loc[mask_overflow, 'Physical_Activity_Hours_Per_Day'] = 0.0

        # 4. Rellenar holgura (<= 24h)
        mask_ok = ~mask_overflow
        df_chunk.loc[mask_ok, 'Physical_Activity_Hours_Per_Day'] = 24 - df_chunk.loc[mask_ok, 'Sum_Hours']
        
        # 5. Redondeo Estético
        all_time_vars = time_vars_independent + ['Physical_Activity_Hours_Per_Day']
        for col in all_time_vars:
            df_chunk[col] = df_chunk[col].round(1)
            
        # Limpieza de otras numéricas
        df_chunk['GPA'] = df_chunk['GPA'].clip(0, 4.0).round(2)
        df_chunk['Age'] = df_chunk['Age'].round(0).astype(int)

        # C. Reversión de Categorías
        for col in cols_categoricas:
            df_chunk[col] = df_chunk[col].round().astype(int)
            max_idx = len(encoders[col].classes_) - 1
            df_chunk[col] = df_chunk[col].clip(0, max_idx)
            df_chunk[col] = encoders[col].inverse_transform(df_chunk[col])
            
        # D. Regla de Negocio: Estrés (Calculada post-corrección de horas)
        condiciones = [
            (df_chunk['Sleep_Hours_Per_Day'] < 6) | (df_chunk['Study_Hours_Per_Day'] > 8), # High
            (df_chunk['Sleep_Hours_Per_Day'] >= 6) & (df_chunk['Study_Hours_Per_Day'] < 6) # Low
        ]
        opciones = ['High', 'Low']
        df_chunk['Stress_Level'] = np.select(condiciones, opciones, default='Moderate')
        
        # E. Asignación de IDs y Escritura
        df_chunk['Student_ID'] = range(current_id, current_id + current_chunk_size)
        current_id += current_chunk_size
        
        # Reordenar columnas para coincidir con original
        df_chunk = df_chunk[columnas_ordenadas]
        
        # APPEND al archivo (header=False para no repetir títulos)
        df_chunk.to_csv(OUTPUT_CSV, mode='a', header=False, index=False)
        
        # F. Actualización de estado
        rows_generated += current_chunk_size
        chunk_counter += 1
        
        # Log de progreso
        elapsed = time.time() - start_time
        speed = rows_generated / elapsed
        eta_min = (TARGET_NEW_ROWS - rows_generated) / speed / 60
        print(f"Chunk {chunk_counter}: {rows_generated:,} filas nuevas. ETA: {eta_min:.1f} min.")
        
        # Liberar RAM
        del df_chunk

    total_rows = len(df_seed) + rows_generated
    print(f"\n--- PROCESO COMPLETADO EXITOSAMENTE ---")
    print(f"Archivo: {OUTPUT_CSV}")
    print(f"Filas Originales: {len(df_seed):,}")
    print(f"Filas Sintéticas: {rows_generated:,}")
    print(f"TOTAL: {total_rows:,}")
    print(f"Tiempo total: {(time.time() - start_time)/60:.1f} minutos")

if __name__ == "__main__":
    main()