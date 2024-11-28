import sys

# Umbrales para clasificar temperaturas
HOT_THRESHOLD = 25
COLD_THRESHOLD = 10

# Ignorar el encabezado del archivo
next(sys.stdin)

for line in sys.stdin:
    # Dividir las columnas del CSV
    data = line.strip().split(",")
    
    if len(data) >= 3:  # Asegurarse de que hay suficientes columnas
        city, date, temp = data[0], data[1], data[2]
        
        try:
            # Convertir la temperatura a float
            temp = float(temp)
            
            # Clasificar y emitir categorías
            if temp >= HOT_THRESHOLD:
                print("hot\t1")
            elif temp <= COLD_THRESHOLD:
                print("cold\t1")
            else:
                print("neutral\t1")
        except ValueError:
            # Saltar líneas con errores en la conversión
            continue
