/*!
 * @file 
 * @brief This file contains implemenation of phong vertex and fragment shader.
 *
 * @author Tomáš Milet, imilet@fit.vutbr.cz
 */

#include<math.h>
#include<assert.h>

#include"student/student_shader.h"
#include"student/gpu.h"
#include"student/uniforms.h"

/// \addtogroup shader_side Úkoly v shaderech
/// @{

void phong_vertexShader(
    GPUVertexShaderOutput     *const output,
    GPUVertexShaderInput const*const input ,
    GPU                        const gpu   ){
  /// \todo Naimplementujte vertex shader, který transformuje vstupní vrcholy do clip-space.<br>
  /// <b>Vstupy:</b><br>
  /// Vstupní vrchol by měl v nultém atributu obsahovat pozici vrcholu ve world-space (vec3) a v prvním
  /// atributu obsahovat normálu vrcholu ve world-space (vec3).<br>
  /// <b>Výstupy:</b><br>
  /// Výstupní vrchol by měl v nultém atributu obsahovat pozici vrcholu (vec3) ve world-space a v prvním
  /// atributu obsahovat normálu vrcholu ve world-space (vec3).
  /// Výstupní vrchol obsahuje pozici a normálu vrcholu proto, že chceme počítat osvětlení ve world-space ve fragment shaderu.<br>
  /// <b>Uniformy:</b><br>
  /// Vertex shader by měl pro transformaci využít uniformní proměnné obsahující view a projekční matici.
  /// View matici čtěte z uniformní proměnné "viewMatrix" a projekční matici čtěte z uniformní proměnné "projectionMatrix".
  /// Zachovejte jména uniformních proměnných a pozice vstupních a výstupních atributů.
  /// Pokud tak neučiníte, akceptační testy selžou.<br>
  /// <br>
  /// Využijte vektorové a maticové funkce.
  /// Nepředávajte si data do shaderu pomocí globálních proměnných.
  /// Pro získání dat atributů použijte příslušné funkce vs_interpret* definované v souboru program.h.
  /// Pro získání dat uniformních proměnných použijte příslušné funkce shader_interpretUniform* definované v souboru program.h.
  /// Vrchol v clip-space by měl být zapsán do proměnné gl_Position ve výstupní struktuře.<br>
  /// <b>Seznam funkcí, které jistě použijete</b>:
  ///  - gpu_getUniformsHandle()
  ///  - getUniformLocation()
  ///  - shader_interpretUniformAsMat4()
  ///  - vs_interpretInputVertexAttributeAsVec3()
  ///  - vs_interpretOutputVertexAttributeAsVec3()
  (void)output;
  (void)input;
  (void)gpu;

  Uniforms const uniformsHandle = gpu_getUniformsHandle(gpu);
  UniformLocation const viewMatrixLocation = getUniformLocation(gpu,"viewMatrix");
  UniformLocation const projectionMatrixLocation = getUniformLocation(gpu,"projectionMatrix");
  
  Mat4 const * const view = shader_interpretUniformAsMat4(uniformsHandle,viewMatrixLocation);
  Mat4 const * const proj = shader_interpretUniformAsMat4(uniformsHandle,projectionMatrixLocation);
  
 
  Vec3 const * in_position = vs_interpretInputVertexAttributeAsVec3(gpu,input,0);
  Vec3 *out_position = vs_interpretOutputVertexAttributeAsVec3(gpu,output,0);  

  Mat4 mvp;
  multiply_Mat4_Mat4(&mvp,proj,view);
 
  Vec4 position4;
  copy_Vec3(out_position,in_position);
  copy_Vec3Float_To_Vec4(&position4,in_position,1.f);
  
  multiply_Mat4_Vec4(&output->gl_Position,&mvp,&position4);
  
  in_position = vs_interpretInputVertexAttributeAsVec3(gpu,input,1);
  out_position = vs_interpretOutputVertexAttributeAsVec3(gpu,output,1);
  
  copy_Vec3(out_position,in_position);

}

float clamp(float d, float min,float max){
   const float t = d < min ? min : d;
   return t > max ? max : t;
}



 

void phong_fragmentShader(
    GPUFragmentShaderOutput     *const output,
    GPUFragmentShaderInput const*const input ,
    GPU                          const gpu   ){
  /// \todo Naimplementujte fragment shader, který počítá phongův osvětlovací model s phongovým stínováním.<br>
  /// <b>Vstup:</b><br>
  /// Vstupní fragment by měl v nultém fragment atributu obsahovat interpolovanou pozici ve world-space a v prvním
  /// fragment atributu obsahovat interpolovanou normálu ve world-space.<br>
  /// <b>Výstup:</b><br> 
  /// Barvu zapište do proměnné color ve výstupní struktuře.<br>
  /// <b>Uniformy:</b><br>
  /// Pozici kamery přečtěte z uniformní proměnné "cameraPosition" a pozici světla přečtěte z uniformní proměnné "lightPosition".
  /// Zachovejte jména uniformních proměnný.
  /// Pokud tak neučiníte, akceptační testy selžou.<br>
  /// <br>
  /// Dejte si pozor na velikost normálového vektoru, při lineární interpolaci v rasterizaci může dojít ke zkrácení.
  /// Zapište barvu do proměnné color ve výstupní struktuře.
  /// Shininess faktor nastavte na 40.f
  /// Difuzní barvu materiálu nastavte na čistou zelenou.
  /// Spekulární barvu materiálu nastavte na čistou bílou.
  /// Barvu světla nastavte na bílou.
  /// Nepoužívejte ambientní světlo.<br>
  /// <b>Seznam funkcí, které jistě využijete</b>:
  ///  - shader_interpretUniformAsVec3()
  ///  - fs_interpretInputAttributeAsVec3()
  (void)output;
  (void)input;
  (void)gpu;

   Uniforms const uniformsHandle = gpu_getUniformsHandle(gpu);

   UniformLocation const lightPosition = getUniformLocation(gpu,"lightPosition");
   UniformLocation const cameraPosition = getUniformLocation(gpu,"cameraPosition");
   
   Vec3 const * lightPos= shader_interpretUniformAsVec3(uniformsHandle,lightPosition);
   Vec3 const * camPos = shader_interpretUniformAsVec3(uniformsHandle,cameraPosition);

   Vec3 const * inter_pos = fs_interpretInputAttributeAsVec3(gpu,input,0);
   Vec3 const * inter_normal = fs_interpretInputAttributeAsVec3(gpu,input,1);
 

   Vec3 V;
   Vec3 v_diff;
   
   sub_Vec3(&v_diff,camPos,inter_pos);
   normalize_Vec3(&V,&v_diff);
  
   Vec3 L;
   Vec3 l_diff;
   
   sub_Vec3(&l_diff,lightPos,inter_pos);
   normalize_Vec3(&L,&l_diff);
   
   Vec3 L_neg;
   sub_Vec3(&L_neg,inter_pos,lightPos);
   normalize_Vec3(&L_neg,&L_neg); 

 
   Vec3 Normal;
   normalize_Vec3(&Normal,inter_normal);

   Vec3 dM;
   Vec3 sM;
   Vec3 dL;
   Vec3 sL;
  
   init_Vec3(&dM,0.f,1.f,0.f);
   init_Vec3(&sM,1.f,1.f,1.f);
   init_Vec3(&dL,1.f,1.f,1.f);
   init_Vec3(&sL,1.f,1.f,1.f);
   
   Vec3 R;
   reflect(&R,&L_neg,&Normal);
  
   float dF;
   float sF;
   
   dF = clamp(dot_Vec3(&Normal,&L),0.f,1.f);
   sF = powf((clamp(dot_Vec3(&R,&V),0.f,1.f)),40.f);
   
   Vec3 diff_color;
   Vec3 spec_color;
   multiply_Vec3_Float(&diff_color,&dM,dF);
   multiply_Vec3_Float(&spec_color,&sM,sF);  
 
   Vec3 phong_color;
   add_Vec3(&phong_color,&diff_color,&spec_color);
  
   copy_Vec3Float_To_Vec4(&output->color,&phong_color,1.f);



}

/// @}
