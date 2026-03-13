from PIL import Image
p = Image.open("C:/Users/vishw/all_tools/UI/test_loader_bottom2.png").load()
print("Bottom area scan:")
for y in range(1400, 2100, 25):
    lr, lg, lb = p[280, y][:3]
    rr, rg, rb = p[800, y][:3]
    print(f"  y={y}: L=({lr},{lg},{lb}), R=({rr},{rg},{rb})")
