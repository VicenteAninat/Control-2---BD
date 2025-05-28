# Etapa 1: Build
FROM node:20-alpine AS builder

WORKDIR /app

COPY . .

RUN npm install
RUN npm run generate

# Etapa 2: Servir con nginx
FROM nginx:stable-alpine

COPY --from=builder /app/.output/public /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]