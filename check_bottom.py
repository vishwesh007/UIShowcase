from PIL import Image
img = Image.open("C:/Users/vishw/all_tools/UI/test_loader_bottom.png")
p = img.load()
for y in range(1400, 2100, 50):
    lr, lg, lb = p[280, y][:3]
    rr, rg, rb = p[800, y][:3]
    print(f"y={y}: L=({lr},{lg},{lb}), R=({rr},{rg},{rb})")
